# Identification
##### Project Name: 
EasyAttendance

##### Group 14:
- Shengtian Tang, B00690131, sh625730@dal.ca
- David Cui, B00788648
- Lan Chen, B00809814
- Navkaran Kumar, B00782012
- Samson Maconi, B00801169
- Xiaoyu Tian, B00692270


# Project Summary
It is an app that allows university professors to take attendance easily and for students to sign attendance easily. It is an app that aims to replace the old way of taking attendance by attendance sheet. Our app can be used all over the world, by professors and students who are familiar or unfamiliar with mobile technology.

## Libraries
**Volley HTTP:** Volley is an open source HTTP library that makes networking for Android apps easier and most importantly, faster. Volley is available on [GitHub](https://github.com/google/volley).

## Installation Notes
Our app has an initial phase in which users need to select their role form teacher and student. Once users have gone through the initial phase, they will not be able to go back and select their role again, our app will remember their choice permanently until the user uninstalled the app. To test our app you will need a minimum of two Android devices (or emulators). The location service must be turned on. If you encountered any UI issues, [here](https://www.samsung.com/ca/smartphones/galaxy-s9/shop/) is the solution.

## Code Examples
You will encounter roadblocks and problems while developing your project. Share 2-3 'problems' that your team solved while developing your project. Write a few sentences that describe your solution and provide a code snippet/block that shows your solution. Example:

**Problem 1: We needed a method to calculate a Fibonacci sequence**

A short description.
```
// The method we implemented that solved our problem
public static int fibonacci(int fibIndex) {
    if (memoized.containsKey(fibIndex)) {
        return memoized.get(fibIndex);
    } else {
        int answer = fibonacci(fibIndex - 1) + fibonacci(fibIndex - 2);
        memoized.put(fibIndex, answer);
        return answer;
    }
}

// Source: Wikipedia Java [1]
```

## Feature Section
- An instructor may teach different courses each year, our app allows instructors to add and edit the course list. They can add a new course by press the "+" button on button right, and edit or view attendance history of an existing course in the list by long press the list item. 

- Instructors can start attendance by click one course on the list.

- Students can sign attendance to the selected class. Only the nearby lectures will be shown in the list, that reduces the chance for students to make mistakes

- Our app provides multi-language access, including English, French, Chinese and Hindi.

## Final Project Status
If we have time, the first thing to do is to add face recognition to our app. Using GPS and face recognition together to ensure that students can only sign-in in the classroom.

We also need to further optimize the structure of the app, such as using server push notifications instead of periodic HTTP requests to reduce data usage.

#### Minimum Functionality
- Users can select their role as Teacher or Student (Completed)
- Teachers can see a list of courses (Completed Completed)
- Teachers can start attendance (Completed)
- Teachers can collect the result (Completed)
- Students can mark attendance (Completed)
- GPS check (Completed)

#### Expected Functionality
- Teachers can add courses to list(Completed)
- Teachers can edit/delete courses list(Completed)

#### Bonus Functionality
- The app can store attendance history (Completed)

## Sources

- [Android Design Guides](https://developer.android.com/design/)
- [Volley] (https://github.com/google/volley)
- [Google Location and Activity Recognition](https://developers.google.com/android/guides/setup)
