package com.example.navkaran.easyattendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaoyutian on 2018-11-06.
 */

public class TakeAttendanceActivity extends AppCompatActivity {

    //for accessing extras
    public static final String COURSE_ID = "COURSE_ID";
    public static final String COURSE_NAME = "COURSE_NAME";
    public static final String COURSE_STUDENT_COUNT = "COURSE_STUDENT_COUNT";
    public static final String ATTENDANCE_COUNT = "ATTENDANCE_COUNT";

    Button stop_btn;
    private TextView class_number;
    private TextView class_name;
    private TextView register_number;
    private TextView check_number;
    private String course_id;
    private String course_name;
    private int student_num;
    private Runnable runnable;
    private Handler handler;
    private int attendanceCount;
    private boolean shouldStop = false;
    private boolean shouldCheck = false;

    private FusedLocationProviderClient mFusedLocationClient;
    private String location_error = "no error";
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        getSupportActionBar().setTitle("Class Attendance");

        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(stop);
        check_number = findViewById(R.id.check_number);
        class_number = findViewById(R.id.class_number);
        class_name = findViewById(R.id.class_name);
        register_number = findViewById(R.id.register_number);

        Intent intent = getIntent();
        course_id = intent.getStringExtra(COURSE_ID);
        course_name = intent.getStringExtra(COURSE_NAME);
        student_num = intent.getIntExtra(COURSE_STUDENT_COUNT, 0);

        class_number.setText(course_id);
        class_name.setText(course_name);
        check_number.setText("not yet started");
        register_number.setText(student_num+" Students Registered");

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(shouldCheck){
                            checkAttendance();
                        }
                    }
                };
                Thread thread = new Thread(null, runnable, "background");
                thread.start();
                handler.postDelayed(this, 2000);
            }
        }, 0);

        setLocation();
    }


    View.OnClickListener stop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(shouldStop){
                handler.removeCallbacksAndMessages(null);
                stopAttendance();
            }else {
                startAttendance(course_id,course_name,longitude,latitude);
                stop_btn.setText(R.string.action_stop_attendance);
                stop_btn.setBackgroundResource(R.drawable.round_button_red_selector);
                shouldCheck = true;
                shouldStop = true;
            }
        }
    };

    private void stopAttendance(){
        Intent intent = new Intent(this, AttendanceDetailsActivity.class);
        intent.putExtra(COURSE_ID, course_id);
        intent.putExtra(COURSE_NAME, course_name);
        intent.putExtra(COURSE_STUDENT_COUNT, student_num);
        intent.putExtra(ATTENDANCE_COUNT, attendanceCount);
        startActivity(intent);
    }

    private void checkAttendance(){
        final String url = "https://web.cs.dal.ca/~stang/csci5708/count.php?class_id="+course_id;
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            attendanceCount = response.getInt("students_count");
                            check_number.setText(attendanceCount+" of "+student_num+" in attendance");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();

            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void startAttendance(String course_id, String course_name, double lon, double lat){
        final String url = "https://web.cs.dal.ca/~stang/csci5708/start_attendance.php?class_info="+course_id+","+course_name+","+lon+","+lat;
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(),"CourseListActivity Error",Toast.LENGTH_SHORT).show();

            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void setLocation(){
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
                        System.out.println("************CourseListActivity************** longitude: "+longitude+"    latitude: "+latitude);
                    }else {
                        location_error = "Unknown Location";
                        System.out.println("**************** "+location_error);
                    }
                }
            });
            return;
        }else{
            location_error = "Permission Denied";
            System.out.println("**************** "+location_error);
        }
    }
}