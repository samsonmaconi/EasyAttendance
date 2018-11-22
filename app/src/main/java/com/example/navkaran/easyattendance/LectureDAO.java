package com.example.navkaran.easyattendance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LectureDAO {

    @Query("SELECT * FROM lectures WHERE lectures.course_key = :courseKey")
    List<Lecture> getLecturesByCourseKey(int courseKey);

    // returns the database auto generated primary key as long
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertLecture(Lecture lecture);

    // return number of rows affected
    @Query("DELETE FROM lectures WHERE lecture_id = :lectureId")
    int deleteByLectureId(long lectureId);

}
