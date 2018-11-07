package com.example.navkaran.easyattendance;

public class CourseItem {

    private String courseID;
    private String courseName;
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
