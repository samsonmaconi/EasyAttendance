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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    //for accessing extras
    public static final String COURSE_ID = "COURSE_ID";
    public static final String COURSE_NAME = "COURSE_NAME";
    public static final String COURSE_STUDENT_COUNT = "COURSE_STUDENT_COUNT";

    public static final int NEW_COURSE_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_COURSE_ACTIVITY_REQUEST_CODE = 2;

    private CourseItemRepository repository;
    private LiveData<List<CourseItem>> courses;

    //UI element
    private ListView lvCourseList;
    private CourseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_course_list);

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
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // Permission has already been granted
        }

        repository = new CourseItemRepository(getApplication());
        courses = repository.getCourses();

        lvCourseList = findViewById(R.id.lvCourses);

        adapter = new CourseAdapter(this, R.layout.course_list_item, courses.getValue());
        lvCourseList.setAdapter(adapter);
        registerForContextMenu(lvCourseList);

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

                startActivity(intent);
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lvCourses) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(adapter.getCourseList().get(info.position).getCourseID());
            String[] menuItems = getResources().getStringArray(R.array.stringArray_course_list_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.stringArray_course_list_menu);
        String menuItemName = menuItems[menuItemIndex];
        CourseItem courseSelected = adapter.getCourseList().get(info.position);

        switch (menuItemName) {
            case "Edit":
                Intent intent = new Intent(CourseListActivity.this, EditCourseActivity.class);
                intent.putExtra(COURSE_ID, courseSelected.getCourseID());
                intent.putExtra(COURSE_NAME, courseSelected.getCourseName());
                intent.putExtra(COURSE_STUDENT_COUNT, courseSelected.getStudentCount());
                startActivityForResult(intent, EDIT_COURSE_ACTIVITY_REQUEST_CODE);
                break;
            case "Delete":
                repository.delete(courseSelected);
                break;
            case "History":
                //TODO
                break;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_COURSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String courseId = data.getStringExtra(COURSE_ID);
            String courseName = data.getStringExtra(COURSE_NAME);
            int courseStudentCount = data.getIntExtra(COURSE_STUDENT_COUNT, 0);
            repository.insert(new CourseItem(courseId, courseName, courseStudentCount));
        } else if (requestCode == EDIT_COURSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String courseId = data.getStringExtra(COURSE_ID);
            String courseName = data.getStringExtra(COURSE_NAME);
            int courseStudentCount = data.getIntExtra(COURSE_STUDENT_COUNT, 0);
            repository.update(new CourseItem(courseId, courseName, courseStudentCount));
        } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Cancelled",
                        Toast.LENGTH_SHORT).show();
        }
    }


}
