package com.example.navkaran.easyattendance;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

// David Cui B00788648 Nov 2018
//data holder for an course item, an entity in the room database
@Entity(tableName = "courses",
        indices={@Index(value="course_key", unique=true)})
public class CourseItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_key")
    private int courseKey;
    //such as CSCI5100
    @ColumnInfo(name = "course_id")
    private String courseID;
    //full name of course
    @ColumnInfo(name = "course_name")
    private String courseName;
    //number of students in course
    @ColumnInfo(name = "course_student_count")
    private int studentCount;

    @Ignore
    public CourseItem(int courseKey, String courseID, String courseName, int studentCount) {
        this.courseKey = courseKey;
        this.courseID = courseID;
        this.courseName = courseName;
        this.studentCount = studentCount;
    }

    public CourseItem(String courseID, String courseName, int studentCount) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.studentCount = studentCount;
    }

    public int getCourseKey() {
        return courseKey;
    }

    public void setCourseKey(int courseKey) {
        this.courseKey = courseKey;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }
}
