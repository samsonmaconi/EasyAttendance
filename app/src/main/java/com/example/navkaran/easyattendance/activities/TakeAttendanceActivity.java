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
import com.example.navkaran.easyattendance.RequestQueueSingleton;
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
    private Button stop_btn;
    private TextView class_number;
    private TextView class_name;
    private TextView register_number;
    private TextView check_number;
    private int courseKey;
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

    private String TAG = "TakeAttendAct";

    private final String FETCH_URL = EasyAttendanceConstants.API_URL+"end_attendance.php?class_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        getSupportActionBar().setTitle(R.string.title_class_attendance);

        //connect with UI
        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(stop);
        check_number = findViewById(R.id.check_number);
        class_number = findViewById(R.id.class_number);
        class_name = findViewById(R.id.class_name);
        register_number = findViewById(R.id.register_number);
        //get database connection
        Intent intent = getIntent();
        courseKey = intent.getIntExtra(EasyAttendanceConstants.COURSE_KEY, -1);
        course_id = intent.getStringExtra(EasyAttendanceConstants.COURSE_ID);
        course_name = intent.getStringExtra(EasyAttendanceConstants.COURSE_NAME);
        student_num = intent.getIntExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, 0);

        class_number.setText(course_id);
        class_name.setText(course_name);
        check_number.setText("");
        register_number.setText(String.format(getString(R.string.formatString_students_registered), student_num));

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
                startAttendance(course_id, course_name, longitude, latitude);
                stop_btn.setText(R.string.action_stop_attendance);
                stop_btn.setBackgroundResource(R.drawable.round_button_red_selector);
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
        intent.putExtra(EasyAttendanceConstants.COURSE_ID, course_id);
        intent.putExtra(EasyAttendanceConstants.COURSE_NAME, course_name);
        intent.putExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, student_num);
        intent.putExtra(EasyAttendanceConstants.ATTENDANCE_COUNT, attendanceCount);
        startActivity(intent);
        finish();
    }

    /**
     * check how many student account already marked attendance
     */
    private void checkAttendance() {
        final String url = EasyAttendanceConstants.API_URL + "count.php?class_id=" + encodeParameter(course_id);
        Log.d(TAG, "checkAttendance URL: " + url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            attendanceCount = response.getInt("students_count");
                            check_number.setText(String.format(getString(R.string.formatString_checked_in), attendanceCount, student_num));
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
    private void startAttendance(String course_id, String course_name, double lon, double lat) {
        final String url = EasyAttendanceConstants.API_URL + "start_attendance.php?class_id=" + encodeParameter(course_id) +
                "&class_name=" + encodeParameter(course_name) +
                "&longitude=" + lon +
                "&latitude=" + lat;

        Log.d(TAG, "startAttendance URL: " + url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        check_number.setText(String.format(getString(R.string.formatString_checked_in), 0, student_num));
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
                        location_error = "Unknown Location";
                        Log.d(TAG, "getLocation: " + location_error);
                    }
                }
            });
            return;
        } else {
            location_error = "Permission Denied";
            Log.d(TAG, "getLocation: " + location_error);
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
        String url = FETCH_URL + encodeParameter(course_id);
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