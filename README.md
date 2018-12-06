
![Easy Attendance](readme_images/logo.png)

# Identification
### Project Title: Easy Attendance
### Group 14:
- David Cui, B00788648, yq506499@dal.ca
- Lan Chen, B00809814, lan.chen@dal.ca
- Navkaran Kumar, B00782012, NKumar@dal.ca
- Samson Maconi, B00801169, samson.maconi@dal.ca
- Shengtian Tang, B00690131, sh625730@dal.ca
- Xiaoyu Tian, B00692270, xy503482@dal.ca

Code Available on [our public Github repo](https://github.com/Navkaran0105/EasyAttendance)

We recommend you to view the dev branch, but you may also view the master branch, which is the same as the dev branch.

# Project Summary
The Easy Attendance application is a Productivity application for Android platform with the promise to simplify the attendance taking process for both teachers and students. It is an app that aims to replace the old way of taking attendance through a paper sheet with the new way of taking attendance through Easy Attendance on Android phones.

This application was developed to be very intuitive and simple to use for the **target audience: 
instructors in academic institutions and their students**.
The application allows instructors to manage a list of the courses they teach. Instructors can select a course and allow students to take attendance for that course. Students can then see the courses that are available for check-in and then check-in to a course. Instructors can see how many students have checked-in 
and can disable check-in for a course after at his discretion. After closing the check-in window, the instructor can then see a list of all students who checked-in successfully; this list is then saved so instructors can access the attendance history in the future. 

The development process featured an agile iterative approach to feature enhancement; with the app incorporating essential features to improve the user experience including Multiple Locales (English, French, Hindi, and Chinese), Haptic Feedback, and GPS Location Verification.

![Why Easy Attendance](readme_images/why.png)

## Backend Logic
The application has a location verification feature, which ensures only students who are actually present in class may check-in. This is implemented with the help of the GPS feature and a custom lightweight `DistanceChecker` utility class. When instructors enable check-in for a course, the application obtains the instructor's GPS location and stores it in a remote database along with the selected *Course ID* and *Course Name* as active lectures. 

On the student client, a query is sent to the remote database's REST API endpoint to obtain the list of active lectures and their GPS locations. The student's client then compared the student's GPS location against the location of each active lecture received from the API using the `DistanceChecker` utility class. Only lectures within the range of the student's location will be available for the student to check-in to ensure students are present in the classroom. 

When students check-in successfully, their information is stored in the remote database. The teacher client retrieves this list from the remote database and stores it in a local SQLite database–after closing the attendance window–for future reference. 

## Backend Service
The backend service for exchanging information between instructor and student includes a database on Dalhousie Bluenose and a set of custom REST APIs written in PHP. When the instructor starts the attendance, 

Below are the valid API endpoints and their usage syntax:

* **Start attendance:** https://web.cs.dal.ca/~stang/csci5708/start_attendance.php?class_info=[class_id],[class_name],[longitude],[latitude]

* **Send student check-in information:** https://web.cs.dal.ca/~stang/csci5708/mark.php?student_info=[student_id],[class_id],[attendance]

* **Get the list of active lectures:** https://web.cs.dal.ca/~stang/csci5708/get_lecture_list.php

* **End attendance and Retrieve students Attendance Log:** https://web.cs.dal.ca/~stang/csci5708/end_attendance.php?class_id=[class_id]

* **Get the number of students who signed-in for a specific class:** https://web.cs.dal.ca/~stang/csci5708/count.php?class_id=[class_id]

## ERD of Local Database
Below is the schema for the local SQLite database with Room Persistence

![Local Database Schema](readme_images/local_db.png)

## Libraries
**Volley HTTP:** Volley is an open source HTTP library that makes networking for Android apps easier
and most importantly, faster. Volley is available on [GitHub](https://github.com/google/volley).

**Room Persistence:** Room Persistence is an android library that simplifies the use of SQLite local
database. It reduces boilerplate codes and validates SQL queries compile-time to reduce errors. Room is 
available on [Android Documentation](https://developer.android.com/training/data-storage/room/).

**Google Play services location API:** It is the location API available in Google Play services used to adding location awareness to our app with automated location tracking. It is available on 
[Google Play Services.](https://developers.google.com/android/guides/setup)

## Installation Notes
To install the application on your device, please follow the following instructions:
1. Clone or download the project into your Android Studio
2. Connect your Android device to your computer
3. Build and Run the project on Android Studio to your Android phone
4. Now, the app is installed on your phone

## First Time Usage Notes
Our app features a one-time user setup where the user will be prompted to select their role (Teacher or
Student), and then their User ID. This information is then stored as a `SharedPreference` to be retrieved at future app launches.
```java
    SharedPreferences sp = getSharedPreferences("CONTAINER",Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString("userID", id);
    editor.putString("userRole", role);
```
After the user has sucessfully gone through the initial setup, he/she will be routed to the Home Activity for the user's selected role (`CheckAttendanceActivity` class for a *Student* user and `CourseListActivity` class for a *Teacher* user). 

To test this app, you will need a minimum of two Android
devices (or emulators). The location service must be turned on and functional.

## Code Examples
**Problem 1: We needed permission to use the user's Location**

If we do not check permission before we request location, the app may crash with the "permission denied" error

```java
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
        != PackageManager.PERMISSION_GRANTED) 
    {
        ActivityCompat.requestPermissions(this, 
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            EasyAttendanceConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
```
Source: [4]

**Problem 2: We had to suspend and reinstate handler message queues to save computation and network
resources**

In the `TakeAttendanceActivity` class for instance, if the teacher decides to perform any of the
following actions after Starting the attendance process:
- Cancel the attendance by hitting the back button 
- Switch to a different app
- Minimise the application
The handlers running on a background thread to update the UI thread will keep running in vain and
therefore keep wasting resources.

To fix this we had to suspend/reinstate handler message queues to save computation and network
resources.

```java
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "onStop: handler callbacks removed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
        getLocation();
        Log.d(TAG, "onResume: handler callbacks added");
    }
```

**Problem 3: SQL operations for the Room Database must be executed on separate threads to ensure
the main UI thread is never blocked**

Whenever we need to perform an SQL operation, such as Create (Insert), Read (Select), Update, Delete,
we must perform the operation on a separate thread because the operation might take a long time and so
it will block the UI thread if it is executed on the UI thread. After studying one of the Google Codelab tutorials, we learned that we can use an AsyncTask class object to solve this problem.

Inside our repository class, we have `AsyncTasks` for each operation where we override the `doInBackground`
method to do the SQL operation in the background thread. Then, a wrapper method instantiates an `AsyncTask` objects and execute it, so the SQL operation is performed in a background thread.

```java
    // AsyncTask that does insert operations on another thread
    private static class InsertAsyncTask extends AsyncTask<CourseItem, Void, Void> {

        private CourseItemDAO asyncTaskDAO;

        InsertAsyncTask(CourseItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Void doInBackground(final CourseItem... courses) {
            asyncTaskDAO.insertCourse(courses[0]);
            return null;
        }
    }
```
Source: [5]

## Feature Section
![High-Level Overview](readme_images/high_level.png)
- **Manage Courses**: A teacher may teach different courses each year, our app allows teachers to
add and edit their course list. They can ***add a new course*** by press the "+" floating action
button, and ***edit an existing course*** or ***delete an existing course*** through the *context menu*
from a long press of the course item.
- **Manage Course Attendance History**: The application also allows a teacher to ***view attendance
history*** of a course on the teacher's list by **long pressing the course item** and selecting History
to navigate to the `AttendanceHistoryActivity` class. This allows the teacher to ***View***, or ***Delete*** historical attendance logs.
- **Capture Class Attendance**: A teacher can start attendance by selecting a course on the list in
the `CourseListActivity` class, which then proceeds to the `TakeAttendanceActivity` class where the
teacher opens the attendance to the students by pressing the Start Attendance button.

- **Students can Sign Attendance**: From the Student home and sole  activity (`CheckAttendanceActivity`
 class) the students can select a class from a dropdown list of ongoing lectures and mark his attendance in a few clicks. Only the nearby lectures will be shown on the list; to reduce the chances of mistakes or attempting to fraudulently mark attendance when away.

- **Multi-Language Support**: For a more personal user experience, the application is available in
multiple locales including English, French, Chinese and Hindi.

- **Haptic Feedback**: For this app, we designed a custom `VibratorUtility` class for delivering
boolean haptic feedback alongside negative or positive Toast messages. Two short bursts for a
negative feedback and one long burst for a positive feedback.

## Final Project Status
At its current state, the project works and fulfils all the proposed functionality. However, there
is still room for improvement.

The Application could benefit from further code optimisations and additional testing. Optimisations
such as using push requests to update the UI on the `TakeAttendanceActivity` as opposed to the current
iterative GET requests to pull updates from the remote database. In addition, because the team was
new to Android development and our design skills are elementary, this project has some weaknesses, for example, 
we were not able to test the app in a large-scale environment where thousands of courses are enabled, and hundreds of thousands of students are checking-in at the same time. More effort can be given in the future to re-design and re-implement the application to make it more robust and efficient.


#### Minimum Functionality
- Users can select their role as *Teacher* or *Student* (Completed)
- Teachers can see a list of courses (Completed)
- Teachers can start attendance for a course(Completed)
- Teachers can stop attendance and see a list of students who checked-in (Completed)
- Students can mark attendance (Completed)
- GPS check to ensure students are actually present (Completed)

#### Expected Functionality
- Teachers can add courses to the course list(Completed)
- Teachers can edit/delete courses in the course list(Completed)

#### Bonus Functionality
- The app can store attendance history and instructors can view or delete them (Completed)
- The app is Multilingual (Completed)
- The app provides haptic feedback (Completed)

![Features](readme_images/features.png)

## Sources

1. [Android Design Guides](https://developer.android.com/design/)
2. [Volley](https://github.com/google/volley)
3. [Google Location and Activity Recognition](https://developers.google.com/android/guides/setup)
4. [Android How to Request Permission](https://developer.android.com/training/permissions/requesting)
5. [Room Persistence Codelab](https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#0)
6. [How to Show Context Menu After Long Clicking](https://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/)
7. [Room Persistence Android Doc](https://developer.android.com/training/data-storage/room/)
