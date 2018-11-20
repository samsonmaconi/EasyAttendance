package com.example.navkaran.easyattendance;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

// entity for Lecture
@Entity(tableName = "lectures",
        foreignKeys = {
                @ForeignKey(onDelete = CASCADE,
                        entity = CourseItem.class,
                        parentColumns = "course_id",
                        childColumns = "course_id")})
@TypeConverters(DateConverter.class)
public class Lecture {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "lecture_id")
    public long lectureId;
    @ColumnInfo(name = "lecture_numAttendee")
    public int numAttendee;
    @ColumnInfo(name = "lecture_date")
    public Date date;
    @ColumnInfo(name = "course_id")
    public String courseId;
}
