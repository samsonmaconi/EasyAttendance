package com.example.navkaran.easyattendance;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AttendanceDetailsActivity extends AppCompatActivity {

    private AttendanceAdapter adapter;
    private ArrayList<AttendanceItem> attendanceItemArrayList;
    private Runnable runnable;


    private ListView lvAttendanceList;
    private Button btnDone;
    private TextView tvCourseId, tvCourseName, tvStudentCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_attendance_list);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.mipmap.ic_launcher_round);

        attendanceItemArrayList = new ArrayList<>();

        lvAttendanceList = findViewById(R.id.lvAttendanceList);
        btnDone = findViewById(R.id.btnDone);
        tvCourseId = findViewById(R.id.tvCourseId);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvStudentCount = findViewById(R.id.tvStudentCount);

        adapter = new AttendanceAdapter(this, R.layout.attendance_list_item, attendanceItemArrayList);
        lvAttendanceList.setAdapter(adapter);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CourseListActivity.class);
                startActivity(i);
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                fetchAttendanceLog();
                populateAttendanceListView();
            }
        };

        new Thread(null, runnable, "Runnable_AttendanceDetails").start();

    }

    private void fetchAttendanceLog() {
    }

    private void populateAttendanceListView() {
        attendanceItemArrayList.add(new AttendanceItem("B00283344", true));
        attendanceItemArrayList.add(new AttendanceItem("B04542373", false));
        attendanceItemArrayList.add(new AttendanceItem("B00283434", true));
        attendanceItemArrayList.add(new AttendanceItem("B00675632", false));
        attendanceItemArrayList.add(new AttendanceItem("B00656554", false));
        attendanceItemArrayList.add(new AttendanceItem("B00211234", true));
        attendanceItemArrayList.add(new AttendanceItem("B00118223", false));
        attendanceItemArrayList.add(new AttendanceItem("B00903930", false));
        attendanceItemArrayList.add(new AttendanceItem("B00930323", false));
        attendanceItemArrayList.add(new AttendanceItem("B00224944", true));
        attendanceItemArrayList.add(new AttendanceItem("B00858749", false));
        attendanceItemArrayList.add(new AttendanceItem("B00455540", false));
        attendanceItemArrayList.add(new AttendanceItem("B00995043", true));
        attendanceItemArrayList.add(new AttendanceItem("B00383494", false));
        attendanceItemArrayList.add(new AttendanceItem("B00801169", true));
    }
}

