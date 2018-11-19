package com.example.navkaran.easyattendance;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

// David Cui B00788648 Nov 2018
//data holder for an course item, an entity in the room database
@Entity
public class CourseItem {

    @PrimaryKey
    public int id;

    //such as CSCI5100
    @ColumnInfo(name = "course_id")
    public String courseID;
    //full name of course
    @ColumnInfo(name = "course_name")
    public String courseName;
    //number of students in course
    @ColumnInfo(name = "course_student_count")
    public int studentCount;

}
