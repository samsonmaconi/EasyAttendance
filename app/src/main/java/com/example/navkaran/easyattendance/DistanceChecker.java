package com.example.navkaran.easyattendance;
/**
 * This is a utility class used to
 * check the distance between the students location
 * and the teachers location against to a range.
 */

import android.location.Location;
import android.util.Log;

public class DistanceChecker {
    private static final float range = 20;
    private static String TAG = "DistanceChecker";

    public static boolean isWithinRange(double student_lon, double student_lat, double teacher_lon, double teacher_lat) {
        Log.d(TAG, "Started");

        Location locStudent = new Location("studentLocation");
        locStudent.setLongitude(student_lon);
        locStudent.setLatitude(student_lat);

        Location locTeacher = new Location("teacherLocation");
        locTeacher.setLongitude(teacher_lon);
        locTeacher.setLatitude(teacher_lat);

        float distance = locTeacher.distanceTo(locStudent);

        if (distance <= range) {
            Log.d(TAG, "isWithinRange : true");
            return true;
        } else {
            Log.d(TAG, "isWithinRange : false");
            return false;
        }
    }

}
