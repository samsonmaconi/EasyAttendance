package com.example.navkaran.easyattendance;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import java.util.List;

// David Cui B00788648 Nov 2018
//activity for course list, where instructors can select the course they want to enable check-in for
public class CourseListActivity extends AppCompatActivity {

    //for accessing extras
    public static final String COURSE_ID = "COURSE_ID";
    public static final String COURSE_NAME = "COURSE_NAME";
    public static final String COURSE_STUDENT_COUNT = "COURSE_STUDENT_COUNT";

    public static final int NEW_COURSE_ACTIVITY_REQUEST_CODE = 1;

    private CourseItemRepository repository;
    private LiveData<List<CourseItem>> courses;

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

        repository = new CourseItemRepository(getApplication());
        courses = repository.getCourses();

        lvCourseList = findViewById(R.id.lvCourses);

        final CourseAdapter adapter = new CourseAdapter(this, R.layout.course_list_item, courses.getValue());
        lvCourseList.setAdapter(adapter);

        courses.observe(this, new Observer<List<CourseItem>>() {
            @Override
            public void onChanged(@Nullable List<CourseItem> courseItems) {
                if(courseItems != null)
                adapter.setCourseList(courseItems);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabNewCourse);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseListActivity.this, NewCourseActivity.class);
                startActivityForResult(intent, NEW_COURSE_ACTIVITY_REQUEST_CODE);
            }
        });

        lvCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.setClass(view.getContext(), TakeAttendanceActivity.class);
                CourseItem course = adapter.getCourseList().get(i);
                intent.putExtra(COURSE_ID, course.getCourseID());
                intent.putExtra(COURSE_NAME, course.getCourseName());
                intent.putExtra(COURSE_STUDENT_COUNT, course.getStudentCount());
                startAttendance(course.getCourseID(),course.getCourseName(),longitude,latitude);
                startActivity(intent);
            }
        });

        setLocation();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_COURSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String courseId = data.getStringExtra(COURSE_ID);
            String courseName = data.getStringExtra(COURSE_NAME);
            int courseStudentCount = data.getIntExtra(COURSE_STUDENT_COUNT, 0);
            repository.insert(new CourseItem(courseId, courseName, courseStudentCount));
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Cancelled",
                    Toast.LENGTH_SHORT).show();
        }
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
