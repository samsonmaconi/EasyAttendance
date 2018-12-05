package com.example.navkaran.easyattendance.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

// David Cui B00788648 Nov 2018

/**
 * Local SQLite database used for storing attendance histories.
 * Room database simplifies using SQLite, reduces boilerplate code.
 * Singleton since RoomDatabase is expensive and we don't need different copies.
 * Entities are tables in the database.
 */
@Database(entities = {CourseItem.class, AttendanceItem.class, Lecture.class}, version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "attend-db";

    // singleton instance
    private static volatile AppDatabase sInstance;

    public abstract CourseItemDAO courseItemDAO();
    public abstract AttendanceItemDAO attendanceItemDAO();
    public abstract LectureDAO lectureDAO();

    // build a persistent database
    // must use worker threads to operate on database, thus must be synchronized
    // parameter: application context
    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                                    .build();
                }
            }
        }
        return sInstance;
    }

}
