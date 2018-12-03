package com.example.navkaran.easyattendance;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

// David Cui Nov 2018

/**
 * required by Room persistence to deal with Data type columns and fields
 */
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
