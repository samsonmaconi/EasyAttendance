package com.example.navkaran.easyattendance;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

// David Cui B00788648 Nov 2018

@Dao
public interface CourseItemDAO {

    @Query("SELECT * FROM courses")
    LiveData<List<CourseItem>> getAllCourses();

    // insert parameters into database in a single transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(CourseItem course);

    // update matches the entities by the primary key
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCourse(CourseItem course);

    // use primary key to find the entity to delete
    @Delete
    void deleteCourse(CourseItem course);

}
