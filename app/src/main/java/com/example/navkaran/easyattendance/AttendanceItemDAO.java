package com.example.navkaran.easyattendance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

// David Cui Nov 2018

/**
 * Data Access Object classes are needed for each table in the database.
 * An abstraction for SQL queries for CRUD operations on the table.
 */
@Dao
public interface AttendanceItemDAO {

    // get all attendances for a particular lecture
    // parameter: ID (primary key) of a lecture
    @Query("SELECT * FROM attendances WHERE attendances.lecture_id = :lectureId")
    List<AttendanceItem> getAttendancesByLectureId(long lectureId);

    // batch insert
    // parameter: varargs or AttendanceItem[] (array)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAttendances(AttendanceItem... attendances);

}
