package com.example.navkaran.easyattendance;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface LectureDAO {

    @Query("SELECT * FROM lectures WHERE lectures.course_key = :courseKey")
    Lecture getLecturesSync(int courseKey);

    // returns the database auto generated primary key as long
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertLecture(Lecture lecture);

    @Delete
    void deleteLecture(Lecture lecture);

}
