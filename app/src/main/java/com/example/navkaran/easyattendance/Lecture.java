package com.example.navkaran.easyattendance;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

// entity for Lecture
@Entity(tableName = "lectures",
        foreignKeys = {
                @ForeignKey(onDelete = CASCADE,
                        entity = CourseItem.class,
                        parentColumns = "course_key",
                        childColumns = "course_key")},
        indices = {@Index(value="course_key")})
@TypeConverters(DateConverter.class)
public class Lecture {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "lecture_id")
    private long lectureId;
    @ColumnInfo(name = "lecture_numAttendee")
    private int numAttendee;
    @ColumnInfo(name = "lecture_date")
    private Date date;
    @ColumnInfo(name = "course_key")
    private int courseKey;

    public Lecture(int numAttendee, Date date, int courseKey) {
        this.numAttendee = numAttendee;
        this.date = date;
        this.courseKey = courseKey;
    }

    public void setLectureId(long lectureId) {
        this.lectureId = lectureId;
    }

    public long getLectureId() {
        return lectureId;
    }

    public int getNumAttendee() {
        return numAttendee;
    }

    public Date getDate() {
        return date;
    }

    public int getCourseKey() {
        return courseKey;
    }
}
