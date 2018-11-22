package com.example.navkaran.easyattendance;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AttendanceItemDAO {

    @Query("SELECT * FROM attendances WHERE attendances.lecture_id = :lectureId")
    LiveData<List<AttendanceItem>> getAttendances(long lectureId);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAttendances(AttendanceItem... attendances);

    @Delete
    void deleteLecture(AttendanceItem attendance);

}
