package com.example.navkaran.easyattendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import java.util.LinkedList;

public class CheckAttendanceActivity extends AppCompatActivity {

    private Runnable runnable;
    private ArrayList<String> classList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private LinkedList<String> classIDList;
    private String classID;
    private Button sign_attendance;
    private Spinner spinner;
    private String lastCheck = "null";
    private String Student_id;
    private Handler handler;

    // to prevent app from crashing when user forget to turn on location services on their device .
    private Double latitude = 0.2333;
    private Double longitude = 0.2333;

    private FusedLocationProviderClient mFusedLocationClient;
    private String location_error = "no error";

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
                        CourseListActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // Permission has already been granted
        }

        Intent intent = getIntent();
        Student_id = intent.getStringExtra("userID");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_check_attendance);

        spinner = findViewById(R.id.spinner);
        sign_attendance = findViewById(R.id.btn_iamhere);
        classList = new ArrayList<>();
        classIDList = new LinkedList<>();
        spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, classList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(select_class);
        sign_attendance.setOnClickListener(sign);

        /*runnable = new Runnable() {
            @Override
            public void run() {
                getClassList();
            }
        };

        Thread thread = new Thread(null, runnable, "background");
        thread.start();*/

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        getClassList();
                    }
                };
                Thread thread = new Thread(null, runnable, "background");
                thread.start();
                handler.postDelayed(this, 2000);
            }
        }, 0);

        setLocation();
    }

    View.OnClickListener sign = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    checkAttendance();
                }
            };

            Thread thread = new Thread(null, runnable, "background");
            thread.start();

        }
    };

    AdapterView.OnItemSelectedListener select_class = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println(position);
            classID = classIDList.get(position);

            if(lastCheck.equals(classID)){
                sign_attendance.setEnabled(false);
                sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
            }else {
                sign_attendance.setEnabled(true);
                sign_attendance.setBackgroundResource(R.drawable.round_button_red);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void checkAttendance(){
        final String url = "https://web.cs.dal.ca/~stang/csci5708/mark.php?student_info=" +Student_id+","+classID+",1";
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String show = "Attend : " + classID +"\n@\n"+"lat: "+latitude+"   lon: "+longitude;
                        Toast.makeText(getApplicationContext(),show , Toast.LENGTH_LONG).show();
                        lastCheck = classID;
                        sign_attendance.setEnabled(false);
                        sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
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

    public void getClassList(){
        final String url = "https://web.cs.dal.ca/~stang/csci5708/get_lecture_list.php";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                        classList.clear();
                        try{
                            for(int i=0; i<response.length(); i++){
                                String class_id = response.getJSONObject(i).getString("class_id");
                                String class_name = response.getJSONObject(i).getString("class_name");
                                double class_lon = response.getJSONObject(i).getDouble("longitude");
                                double class_lat = response.getJSONObject(i).getDouble("latitude");
                                //int class_state = response.getJSONObject(i).getInt("state");
                                int class_state = 1;
                                System.out.println("longitude: "+longitude+" latitude: "+latitude);
                                if(new Class("("+class_id + ") " + class_name,
                                        class_lon,class_lat,longitude,latitude,20,class_state).isAbleToCheckIn()){
                                    classList.add("("+class_id + ") " + class_name);
                                    classIDList.add(class_id);

                                }else{
                                    //classList.add("("+class_id + ") " + class_name);
                                }
                                if(classList.isEmpty()){
                                    sign_attendance.setEnabled(false);
                                    sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
                                }
                            }
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }catch (JSONException e){
                            sign_attendance.setEnabled(false);
                            sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sign_attendance.setEnabled(false);
                sign_attendance.setBackgroundResource(R.drawable.round_button_disabled);
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
                        System.out.println("************CheckAttendanceActivity************** longitude: "+longitude+"    latitude: "+latitude);
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
