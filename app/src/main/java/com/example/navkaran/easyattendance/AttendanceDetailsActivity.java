package com.example.navkaran.easyattendance;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class AttendanceDetailsActivity extends AppCompatActivity {

    private AttendanceAdapter adapter;
    private ArrayList<AttendanceItem> attendanceItemArrayList;
    private ListView lvAttendanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        attendanceItemArrayList = new ArrayList<>();
        lvAttendanceList = findViewById(R.id.lvAttendanceList);

        adapter = new AttendanceAdapter(this, R.layout.attendance_list_item, attendanceItemArrayList);
        lvAttendanceList.setAdapter(adapter);

        populateAttendanceList();
    }

    private void populateAttendanceList() {
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

