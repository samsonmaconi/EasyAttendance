package com.example.navkaran.easyattendance;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

// David Cui Nov 2018

/**
 * Data Access Object for Lecture entity, has the SQL queries for Lecture table
 */
@Dao
public interface LectureDAO {

    // get all lectures of a course in LiveData
    @Query("SELECT * FROM lectures WHERE lectures.course_key = :courseKey")
    LiveData<List<Lecture>> getLiveLecturesByCourseKey(int courseKey);

    // get all lectures of a course not in LiveData
    @Query("SELECT * FROM lectures WHERE lectures.course_key = :courseKey")
    List<Lecture> getLecturesByCourseKey(int courseKey);

    // returns the database auto generated primary key as long
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertLecture(Lecture lecture);

    // delete a particular lecture, note that this is delete on cascade
    // so all attendances linked to that lecture is also deleted
    @Query("DELETE FROM lectures WHERE lecture_id = :lectureId")
    void deleteByLectureId(long lectureId);

}
