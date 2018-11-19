package com.example.navkaran.easyattendance;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

// David Cui B00788648 Nov 2018

@Dao
public interface CourseItemDAO {

    @Query("SELECT * FROM CourseItem")
    LiveData<List<CourseItem>> getAllCourses();

    @Insert(onConflict = IGNORE)
    void insertCourse(CourseItem course);

    @Update(onConflict = REPLACE)
    void updateCourse(CourseItem course);

    @Delete
    void deleteCourse(CourseItem course);

}
