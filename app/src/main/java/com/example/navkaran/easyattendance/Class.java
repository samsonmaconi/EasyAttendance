package com.example.navkaran.easyattendance;

import android.location.Location;

public class Class {
    private String class_name;
    private Boolean ableToCheckIn;
    private Location classroom;

    public Class(String class_name, Double classroom_lon, Double classroom_lat,
                 Double student_lon, Double student_lat, float range){
        this.class_name = class_name;
        classroom = new Location(class_name);
        classroom.setLatitude(classroom_lat);
        classroom.setLongitude(classroom_lon);
        Check(student_lon,student_lat,range);
    }

    public void Check(Double lon, Double lat, float range){
        Location mylocation = new Location("mylocation");
        mylocation.setLongitude(lon);
        mylocation.setLatitude(lat);
        float distance = classroom.distanceTo(mylocation);

        if(distance <= range){
            ableToCheckIn = true;
        }else {
            ableToCheckIn = false;
        }
    }

    public Boolean isAbleToCheckIn() {
        return ableToCheckIn;
    }
}
