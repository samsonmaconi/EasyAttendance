package com.example.navkaran.easyattendance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AttendanceItemDAO {

    @Query("SELECT * FROM attendances WHERE attendances.lecture_id = :lectureId")
    List<AttendanceItem> getAttendancesByLectureId(long lectureId);

    // batch insert, takes varargs or AttendanceItem[] (array)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAttendances(AttendanceItem... attendances);

}
