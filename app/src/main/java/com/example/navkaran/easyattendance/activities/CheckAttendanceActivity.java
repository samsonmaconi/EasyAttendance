package com.example.navkaran.easyattendance.activities;

// Shengtian Tang

/**
 * This is the only activity seen by the student
 *
 * This activity will check current lectures through API calls periodically,
 * only near by lectures will be displayed.
 *
 * Once student signed-in, the button will be disabled.
 *
 * The button will be enabled again when students select another near by lecture, so that
 * they can correct their mistakes if they signed-in to a wrong lecture.
 *
 * */

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
import com.example.navkaran.easyattendance.utils.DistanceChecker;
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
import java.util.ArrayList;

public class CheckAttendanceActivity extends AppCompatActivity {

    private Button signAttendance;
    private Spinner spinner;
    private Runnable runnable;
    private Handler handler;
    private ArrayList<String> classList; // list of class name (Mobile Computing)
    private ArrayList<String> classIDString; // list of class id (e.g. CSCI-5708)
    private ArrayList<Integer> classIDList; // list of class number id (primary key)
    private ArrayAdapter<String> spinnerArrayAdapter;
    private int classID;
    private String classStringID;
    private int lastCheck = -1;
    private String studentId;
    private String locationError = "";
    private Double studentLatitude = 0.0;
    private Double studentLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;

    private final String TAG = "CheckAttendance"; //for testing and debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        // gets permission in case users removed permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    EasyAttendanceConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        getSupportActionBar().setTitle(R.string.title_attendance_home);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("userID");

        spinner = findViewById(R.id.spinner);
        signAttendance = findViewById(R.id.btnMarkAttendance);
        classList = new ArrayList<>();
        classIDList = new ArrayList<>();
        classIDString = new ArrayList<>();
        spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, classList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(onClassSelected);
        signAttendance.setOnClickListener(onBtnMarkAttendanceClick);


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
                        handler.postDelayed(this, 3000);
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
            Log.d(TAG, "Position: " + position);
            if(position == 0){
                signAttendance.setEnabled(false);
                signAttendance.setBackgroundResource(R.drawable.round_button_disabled);
            }else {
                classID = classIDList.get(position - 1); //-1 to Accounts for the hint
                classStringID = classIDString.get(position-1); //-1 to Accounts for the hint
                if (lastCheck == classID) {
                    VibratorUtility.vibrate(getApplicationContext(), true);
                    Toast.makeText(CheckAttendanceActivity.this, String.format(getString(R.string.formatString_alert_failure_duplicate), classStringID), Toast.LENGTH_LONG).show();
                    signAttendance.setEnabled(false);
                    signAttendance.setBackgroundResource(R.drawable.round_button_disabled);
                } else {
                    signAttendance.setEnabled(true);
                    signAttendance.setBackgroundResource(R.drawable.round_button_orange_selector);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //send HTTP request to our DB to sign in
    public void markAttendance() {
        final String url = EasyAttendanceConstants.API_URL + "mark.php?student_info=" + encodeParameter(studentId) + "," + encodeParameter(classStringID) + ",1";
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        lastCheck = classID;
                        signAttendance.setEnabled(false);
                        signAttendance.setBackgroundResource(R.drawable.round_button_disabled);
                        VibratorUtility.vibrate(getApplicationContext(),false);
                        Toast.makeText(CheckAttendanceActivity.this, String.format(getString(R.string.formatString_alert_success), classStringID), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VibratorUtility.vibrate(getApplicationContext(),false);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.formatString_alert_failure_closed), classStringID), Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    // get course list from the API. only available course will be displayed
    public void getCourseList() {
        final String url = EasyAttendanceConstants.API_URL + "get_lecture_list.php";
        System.out.println(url);
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "ClassList Response: Success");
                        int primarykeyID;
                        String class_id;
                        String class_name;
                        double class_lon;
                        double class_lat;
                        classList.clear();
                        classIDList.clear();
                        classIDString.clear();
                        classList.add(getString(R.string.hint_please_select_a_course));
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                primarykeyID = response.getJSONObject(i).getInt("id");
                                class_id = response.getJSONObject(i).getString("class_id");
                                class_name = response.getJSONObject(i).getString("class_name");
                                class_lon = response.getJSONObject(i).getDouble("longitude");
                                class_lat = response.getJSONObject(i).getDouble("latitude");
                                Log.d(TAG, "studentLongitude: " + studentLongitude + " studentLatitude: " + studentLatitude);
                                if (DistanceChecker.isWithinRange(studentLongitude, studentLatitude, class_lon, class_lat)) {
                                    Log.d(TAG, "Distance Checker: Success");
                                    classList.add("(" + class_id + ") " + class_name);
                                    classIDList.add(primarykeyID);
                                    classIDString.add(class_id);
                                }
                                Log.d(TAG, "After Distance Checker");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (classList.isEmpty()) {
                            Log.d(TAG, "classList : isEmpty");
                            classIDList.clear();
                            signAttendance.setEnabled(false);
                            signAttendance.setBackgroundResource(R.drawable.round_button_disabled);
                            spinnerArrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "classList : isNotEmpty");
                            spinnerArrayAdapter.notifyDataSetChanged();
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
                    message = "No lecture available now. Please try again after some time!!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

                Log.d(TAG, "getCourseList Error: " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                //disable the button once sign-in is done
                classIDList.clear();
                signAttendance.setEnabled(false);
                signAttendance.setBackgroundResource(R.drawable.round_button_disabled);
                spinnerArrayAdapter.notifyDataSetChanged();
            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }


    // use google location service to find user's location
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
                        // save studentLongitude and studentLatitude to a local variable for future use
                        studentLongitude = location.getLongitude();
                        studentLatitude = location.getLatitude();
                        Log.d(TAG, "Get User Location: lon: " + studentLongitude + ", lat: " + studentLatitude);
                    } else {
                        locationError = "Unknown Location";
                        Log.d(TAG, "Get User Location: " + locationError);
                    }
                }
            });
            return;
        } else {
            locationError = "Permission Denied";
            Log.d(TAG, "Get User Location: " + locationError);
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
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "onStop: handler callbacks removed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
        Log.d(TAG, "onResume: handler callbacks added");
    }

}
