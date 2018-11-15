package com.example.navkaran.easyattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        getSupportActionBar().setTitle("Course List");

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
                startActivity(intent);
            }
        });
    }

    //fake data for the sake of trying out the layout
    private void populateCourseList() {
        courseItemArrayList.add(new CourseItem("CSCI 1000", "1st Year Course", 120));
        courseItemArrayList.add(new CourseItem("CSCI 2000", "2nd Year Course", 90));
        courseItemArrayList.add(new CourseItem("CSCI 3000", "3rd Year Course", 60));
        courseItemArrayList.add(new CourseItem("CSCI 4000", "4th Year Course", 30));
        courseItemArrayList.add(new CourseItem("CSCI 5000", "Grad Course 1", 90));
        courseItemArrayList.add(new CourseItem("CSCI 6000", "Grad Course 2", 30));
    }
}
