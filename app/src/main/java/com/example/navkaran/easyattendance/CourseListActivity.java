package com.example.navkaran.easyattendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;

// David Cui B00788648 Nov 2018
//activity for course list, where instructors can select the course they want to enable check-in for
public class CourseListActivity extends AppCompatActivity {

    //for accessing extras
    public static final String COURSE_ID = "COURSE_ID";
    public static final String COURSE_NAME = "COURSE_NAME";
    public static final String COURSE_STUDENT_COUNT = "COURSE_STUDENT_COUNT";

    private CourseAdapter adapter;
    //the list of courses this instructor is teaching
    private ArrayList<CourseItem> courseItemArrayList;
    //UI element
    private ListView lvCourseList;

    private FusedLocationProviderClient mFusedLocationClient;
    private String location_error = "no error";
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_course_list);

        courseItemArrayList = new ArrayList<>();
        lvCourseList = findViewById(R.id.lvCourses);

        adapter = new CourseAdapter(this, R.layout.course_list_item, courseItemArrayList);
        lvCourseList.setAdapter(adapter);

        populateCourseList();

        //TODO
        // get extra from intent (instructor id)
        // get course list from file or database instead of hard coding

        lvCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.setClass(view.getContext(), TakeAttendanceActivity.class);
                CourseItem course = courseItemArrayList.get(i);
                intent.putExtra(COURSE_ID, course.getCourseID());
                intent.putExtra(COURSE_NAME, course.getCourseName());
                intent.putExtra(COURSE_STUDENT_COUNT, course.getStudentCount());
                startAttendance(course.getCourseID(),course.getCourseName(),longitude,latitude);
                startActivity(intent);
            }
        });

        setLocation();
    }

    //fake data for the sake of trying out the layout
    private void populateCourseList() {
        courseItemArrayList.add(new CourseItem("CSCI-1010", "1st Year Course", 120));
        courseItemArrayList.add(new CourseItem("CSCI-2132", "2nd Year Course", 90));
        courseItemArrayList.add(new CourseItem("CSCI-3171", "3rd Year Course", 60));
        courseItemArrayList.add(new CourseItem("CSCI-4790", "4th Year Course", 30));
        courseItemArrayList.add(new CourseItem("CSCI-5708", "Grad Course 1", 90));
        courseItemArrayList.add(new CourseItem("CSCI-5100", "Grad Course 2", 30));
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
                        System.out.println("************MainActivity************** longitude: "+longitude+"    latitude: "+latitude);
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
