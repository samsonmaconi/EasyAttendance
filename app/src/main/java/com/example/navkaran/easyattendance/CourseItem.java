package com.example.navkaran.easyattendance;

// David Cui B00788648 Nov 2018
//data holder for an course item
public class CourseItem {

    //such as CSCI5100
    private String courseID;
    //full name of course
    private String courseName;
    //number of students in course
    private int studentCount;

    public CourseItem(String courseID, String courseName, int studentCount) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.studentCount = studentCount;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getStudentCount() {
        return studentCount;
    }
}
