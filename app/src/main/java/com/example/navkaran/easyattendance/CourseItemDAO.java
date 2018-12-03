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

/**
 * Data Access Object for CourseItem, contains the SQL queries needed for CRUD operations
 */
@Dao
public interface CourseItemDAO {

    // LiveData library class makes data observation easier, so updating view when data changes
    // is easier, reducing dependency
    // index all courses
    @Query("SELECT * FROM courses ORDER BY course_id")
    LiveData<List<CourseItem>> getAllCourses();

    // insert a course into database in a single transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertCourse(CourseItem course);

    // update matches the entities by the primary key
    @Update(onConflict = OnConflictStrategy.ABORT)
    void updateCourse(CourseItem course);

    // use primary key to find the entity to delete
    // this is delete on cascade, so all lectures and attendances for this course
    // is also deleted
    @Delete
    void deleteCourse(CourseItem course);

}
