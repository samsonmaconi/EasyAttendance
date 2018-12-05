package com.example.navkaran.easyattendance.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.navkaran.easyattendance.utils.EasyAttendanceConstants;
import com.example.navkaran.easyattendance.R;
import com.example.navkaran.easyattendance.utils.RequestQueueSingleton;
import com.example.navkaran.easyattendance.utils.VibratorUtility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by xiaoyutian on 2018-11-06.
 */

public class TakeAttendanceActivity extends AppCompatActivity {

    //for accessing extras

    // attribute need
    private Button stopBtn;
    private TextView classNumber;
    private TextView className;
    private TextView registerNumber;
    private TextView checkNumber;
    private int courseKey;
    private String courseId;
    private String courseName;
    private int studentNum;
    private Runnable runnable;
    private Handler handler;
    private int attendanceCount;
    private boolean shouldStop = false;
    private boolean shouldCheck = false;

    private FusedLocationProviderClient mFusedLocationClient;
    private String locationError = "no error";
    private double latitude;
    private double longitude;

    private String TAG = "TakeAttendAct";

    private final String FETCH_URL = EasyAttendanceConstants.API_URL+"end_attendance.php?class_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        getSupportActionBar().setTitle(R.string.title_class_attendance);

        //connect with UI
        stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(stop);
        checkNumber = findViewById(R.id.tvCheckNumber);
        classNumber = findViewById(R.id.tvClassNumber);
        className = findViewById(R.id.tvClassName);
        registerNumber = findViewById(R.id.tvRegisterNumber);
        //get database connection
        Intent intent = getIntent();
        courseKey = intent.getIntExtra(EasyAttendanceConstants.COURSE_KEY, -1);
        courseId = intent.getStringExtra(EasyAttendanceConstants.COURSE_ID);
        courseName = intent.getStringExtra(EasyAttendanceConstants.COURSE_NAME);
        studentNum = intent.getIntExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, 0);

        classNumber.setText(courseId);
        className.setText(courseName);
        checkNumber.setText("");
        registerNumber.setText(String.format(getString(R.string.formatString_students_registered), studentNum));

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        if (shouldCheck) {
                            Log.d(TAG, "Inner Runnable: checkAttendance() called");
                            checkAttendance();
                        }
                    }
                };
                Thread thread = new Thread(null, r, "Check Attendance");
                thread.start();
                handler.postDelayed(this, 2000);
            }
        };
    }


    View.OnClickListener stop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (shouldStop) {
                handler.removeCallbacksAndMessages(null);
                stopAttendance();
            } else {
                startAttendance(courseId, courseName, longitude, latitude);
                stopBtn.setText(R.string.action_stop_attendance);
                stopBtn.setBackgroundResource(R.drawable.round_button_red_selector);
                shouldCheck = true;
                shouldStop = true;
            }
        }
    };

    /**
     * stop Attendance function
     */
    private void stopAttendance() {
        Intent intent = new Intent(this, AttendanceDetailsActivity.class);
        intent.putExtra(EasyAttendanceConstants.COURSE_KEY, courseKey);
        intent.putExtra(EasyAttendanceConstants.COURSE_ID, courseId);
        intent.putExtra(EasyAttendanceConstants.COURSE_NAME, courseName);
        intent.putExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, studentNum);
        intent.putExtra(EasyAttendanceConstants.ATTENDANCE_COUNT, attendanceCount);
        startActivity(intent);
        finish();
    }

    /**
     * check how many student account already marked attendance
     */
    private void checkAttendance() {
        final String url = EasyAttendanceConstants.API_URL + "count.php?class_id=" + encodeParameter(courseId);
        Log.d(TAG, "checkAttendance URL: " + url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            attendanceCount = response.getInt("students_count");
                            checkNumber.setText(String.format(getString(R.string.formatString_checked_in), attendanceCount, studentNum));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                String message = "message";
                if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet. Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

                Log.d(TAG, "checkAttendance Error: " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                VibratorUtility.vibrate(getApplicationContext(),false);

            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    /**
     * start attendance
     */
    private void startAttendance(String courseId, String courseName, double lon, double lat) {
        final String url = EasyAttendanceConstants.API_URL + "start_attendance.php?class_id=" + encodeParameter(courseId) +
                "&class_name=" + encodeParameter(courseName) +
                "&longitude=" + lon +
                "&latitude=" + lat;

        Log.d(TAG, "startAttendance URL: " + url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        checkNumber.setText(String.format(getString(R.string.formatString_checked_in), 0, studentNum));
                        VibratorUtility.vibrate(getApplicationContext(),true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                String message = "message";
                if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet. Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

                Log.d(TAG, "startAttendance Error: " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                VibratorUtility.vibrate(getApplicationContext(),false);
            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    /**
     * get teacher's location with GPS feature
     */
    private void getLocation() {
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
                        Log.d(TAG, "getLocation: " + "teacher lon: " + longitude + " teacher lat: " + latitude);
                    } else {
                        locationError = "Unknown Location";
                        Log.d(TAG, "getLocation: " + locationError);
                    }
                }
            });
            return;
        } else {
            locationError = "Permission Denied";
            Log.d(TAG, "getLocation: " + locationError);
        }
    }

    private String encodeParameter(String s){
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
                fetchAttendanceLog();
                super.onOptionsItemSelected(menuItem);
            }
        }
        return false;
    }

    private void fetchAttendanceLog() {
        String url = FETCH_URL + encodeParameter(courseId);
        Log.d(TAG, "Request URL: " + url);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String studentId;

                for(int i = 0; i <response.length(); i++ ){
                    try {
                        studentId = response.getJSONObject(i).getString("student_id");
                        Log.d(TAG, "JSONResponse Loop: Student ID: " + studentId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );

        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "onStop: handler callbacks removed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
        getLocation();
        Log.d(TAG, "onResume: handler callbacks added");
    }
}