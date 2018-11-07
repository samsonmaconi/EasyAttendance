package com.example.navkaran.easyattendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity {

    private CourseAdapter adapter;
    private ArrayList<CourseItem> courseItemArrayList;
    private ListView lvCourseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        courseItemArrayList = new ArrayList<>();
        lvCourseList = findViewById(R.id.lvCourses);

        adapter = new CourseAdapter(this, R.layout.course_list_item, courseItemArrayList);
        lvCourseList.setAdapter(adapter);

        populateCourseList();
    }

    private void populateCourseList() {
        courseItemArrayList.add(new CourseItem("CSCI 1000", "1st Year Course", 120));
        courseItemArrayList.add(new CourseItem("CSCI 2000", "2nd Year Course", 90));
        courseItemArrayList.add(new CourseItem("CSCI 3000", "3rd Year Course", 60));
        courseItemArrayList.add(new CourseItem("CSCI 4000", "4th Year Course", 30));
        courseItemArrayList.add(new CourseItem("CSCI 5000", "Grad Course 1", 90));
        courseItemArrayList.add(new CourseItem("CSCI 6000", "Grad Course 2", 30));
    }
}
