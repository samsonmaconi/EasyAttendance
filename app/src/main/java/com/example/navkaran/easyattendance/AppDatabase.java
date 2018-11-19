package com.example.navkaran.easyattendance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

// David Cui B00788648 Nov 2018

@Database(entities = {CourseItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract CourseItemDAO courseModel();

    public static AppDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
