package com.example.navkaran.easyattendance;

import android.location.Location;

public class Class {
    private String class_name;
    private boolean ableToCheckIn;
    private Location classroom;

    public Class(String class_name, double classroom_lon, double classroom_lat,
                 double student_lon, double student_lat, float range, int state){
        this.class_name = class_name;
        classroom = new Location(class_name);
        classroom.setLatitude(classroom_lat);
        classroom.setLongitude(classroom_lon);
        Check(student_lon,student_lat,range,state);
    }

    public void Check(double lon, double lat, float range,int state){
        if(state == 1){
            Location mylocation = new Location("mylocation");
            mylocation.setLongitude(lon);
            mylocation.setLatitude(lat);
            float distance = classroom.distanceTo(mylocation);
            System.out.println("1: "+state);

            if(distance <= range){
                ableToCheckIn = true;
            }else {
                ableToCheckIn = false;
            }
        }else{
            System.out.println("0: "+state);

            ableToCheckIn = false;
        }
    }

    public boolean isAbleToCheckIn() {
        return ableToCheckIn;
    }
}
