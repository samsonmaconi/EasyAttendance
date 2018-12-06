package com.example.navkaran.easyattendance.utils;

import android.location.Location;
import android.util.Log;

/**
 * @author Samson Maconi
 * Nov 2018
 * This is a utility class used to
 * check the distance between the students location
 * and the teachers location against to a range.
 */
public class DistanceChecker {
    private static final float range = 20;//Acceptable range in Meters to allow student check-in
    private static String TAG = "DistanceChecker"; //For Testing and Debugging

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
