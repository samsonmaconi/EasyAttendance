package com.example.navkaran.easyattendance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

// David Cui B00788648 Nov 2018

// singleton since RoomDatabase is expensive
@Database(entities = {CourseItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "attend-db";

    // singleton instance
    private static AppDatabase sInstance;

    public abstract CourseItemDAO courseModel();

    // build a persistent database
    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .build();
        }
        return sInstance;
    }


    public static void destroyInstance() {
        sInstance = null;
    }
}
