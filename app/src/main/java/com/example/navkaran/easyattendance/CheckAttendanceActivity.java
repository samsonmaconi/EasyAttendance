package com.example.navkaran.easyattendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckAttendanceActivity extends AppCompatActivity {

    private Button sign_attendance;
    private Spinner spinner;
    private Runnable runnable;
    private Handler handler;
    private ArrayList<String> classList, classIDList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String classId;
    private String lastCheck = "";
    private String studentId;
    private String location_error = "";
    private Double student_latitude = 0.0;
    private Double student_longitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;

    private final String TAG = "CheckAttendance"; //for testing and debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        EasyAttendanceConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // Permission has already been granted
        }

        getSupportActionBar().setTitle(R.string.title_attendance_home);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("userID");

        spinner = findViewById(R.id.spinner);
        sign_attendance = findViewById(R.id.btn_markAttendance);
        classList = new ArrayList<>();
        classIDList = new ArrayList<>();
        spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, classList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(onClassSelected);
        sign_attendance.setOnClickListener(onBtnMarkAttendanceClick);


        //This block of code refreshes the list of Courses through the API every 30000 milliseconds
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getUserLocation();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                getCourseList();
                            }
                        };
                        Thread thread = new Thread(null, runnable, "SetLocation_GetClassList");
                        thread.start();
                        handler.postDelayed(this, 30000);
                    }
                });

            }
        };
        Thread thread = new Thread(null, runnable, "background");
        thread.start();
    }

    View.OnClickListener onBtnMarkAttendanceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    markAttendance();
                }
            };

            Thread thread = new Thread(null, runnable, "background");
            thread.start();

        }
    };

    AdapterView.OnItemSelectedListener onClassSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println(position);
            classId = classIDList.get(position);

            if (lastCheck.equals(classId)) {
                VibratorUtility.vibrate(getApplicationContext(),true);
                Toast.makeText(CheckAttendanceActivity.this, String.format(getString(R.string.formatString_alert_failure_duplicate), classId), Toast.LENGTH_LONG).show();
                sign_attendance.setEnabled(false);
                sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
            } else {
                sign_attendance.setEnabled(true);
                sign_attendance.setBackgroundResource(R.drawable.round_button_orange_selector);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    public void markAttendance() {
        final String url = "https://web.cs.dal.ca/~stang/csci5708/mark.php?student_info=" + studentId + "," + classId + ",1";
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        lastCheck = classId;
                        sign_attendance.setEnabled(false);
                        sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
                        VibratorUtility.vibrate(getApplicationContext(),false);
                        Toast.makeText(CheckAttendanceActivity.this, String.format(getString(R.string.formatString_alert_success), classId), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VibratorUtility.vibrate(getApplicationContext(),false);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.formatString_alert_failure_closed), classId), Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public void getCourseList() {
        final String url = "https://web.cs.dal.ca/~stang/csci5708/get_lecture_list.php";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "ClassList Response: Success");
                        String class_id;
                        String class_name;
                        double class_lon;
                        double class_lat;
                        classList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                class_id = response.getJSONObject(i).getString("class_id");
                                class_name = response.getJSONObject(i).getString("class_name");
                                class_lon = response.getJSONObject(i).getDouble("longitude");
                                class_lat = response.getJSONObject(i).getDouble("latitude");
                                Log.d(TAG, "student_longitude: " + student_longitude + " student_latitude: " + student_latitude);
                                if (DistanceChecker.isWithinRange(student_longitude, student_latitude, class_lon, class_lat)) {
                                    Log.d(TAG, "Distance Checker: Success");
                                    classList.add("(" + class_id + ") " + class_name);
                                    classIDList.add(class_id);
                                }
                                Log.d(TAG, "After Distance Checker");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (classList.isEmpty()) {
                            Log.d(TAG, "classList : isEmpty");
                            classIDList.clear();
                            sign_attendance.setEnabled(false);
                            sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
                        } else {
                            Log.d(TAG, "classList : isNotEmpty");
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ClassList Response: Volley Error");
                error.printStackTrace();
                classIDList.clear();
                sign_attendance.setEnabled(false);
                sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
                spinnerArrayAdapter.notifyDataSetChanged();
            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }


    private void getUserLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Get last known location. In some rare situations this can be null.
                    if (location != null) {
                        // save student_longitude and student_latitude to a local variable for future use
                        student_longitude = location.getLongitude();
                        student_latitude = location.getLatitude();
                        Log.d(TAG, "Get User Location: lon: " + student_longitude + ", lat: " + student_latitude);
                    } else {
                        location_error = "Unknown Location";
                        Log.d(TAG, "Get User Location: " + location_error);
                    }
                }
            });
            return;
        } else {
            location_error = "Permission Denied";
            Log.d(TAG, "Get User Location: " + location_error);
        }
    }
}
