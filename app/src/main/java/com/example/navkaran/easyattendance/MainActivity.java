package com.example.navkaran.easyattendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private Button refresh;
    private TextView testlocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private String location_error = "no error";
    private Double longitude,latitude;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = findViewById(R.id.btn);
        testlocation = findViewById(R.id.testlocation);

        refresh.setOnClickListener(f5);
        
        // Put all the code you want to add to onCreat() in the following block. 
        // **************** start ****************
        
        
        
        
        /* TODO: read variable
        
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        // getString([key],[default value]);
        String user_role = sharedPref.getString("role","XXX"); 
        
        switch (user_role){
            case "instructor":
                intent = new Intent(this, CourseListActivity.class);
                startActivity(intent);
            case "student":
                intent = new Intent(this, CheckAttendanceActivity.class);
                startActivity(intent);
            case "XXX":
                intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
            default:
                break;
        }
        */



        //****************   end   ****************
        
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // save longitude and latitude to a local variable for future use
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        startNewIntend(longitude,latitude);
                    }else {
                        location_error = "Unknown Location";
                    }
                }
            });
            return;
        }else{
            location_error = "Permission Denied";
        }
    }

    View.OnClickListener f5 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),location_error,Toast.LENGTH_SHORT).show();
            testlocation.setText("lon: "+longitude+"  lat: "+latitude);
            System.out.println("lon: "+longitude+"  lat: "+latitude);
        }
    };

    private void startNewIntend(Double longitude, Double latitude){
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra("LONGITUDE", longitude);
        intent.putExtra("LATITUDE", latitude);
        startActivity(intent);
    }
}
