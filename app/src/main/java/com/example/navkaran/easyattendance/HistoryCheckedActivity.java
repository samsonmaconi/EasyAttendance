package com.example.navkaran.easyattendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HistoryCheckedActivity extends AppCompatActivity {

    private ListView lvAttendanceList;
    private Button btnDone;
    private TextView tvCourseId, tvCourseName, tvStudentCount, tvAttendanceSummary;
    private long lectureId;
    private String courseId;
    private String courseName;
    private int studentAccountRegister;
    private int studentAcountChecked;


    private ArrayList<AttendanceItem> attendanceItemList;
    private AttendanceAdapter attendanceAdapter;

    private AttendanceItemRepository attendanceItemRepository;
    private LectureRepository lectureRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        lvAttendanceList = findViewById(R.id.lvAttendanceList);
        btnDone = findViewById(R.id.btnDone);
        tvCourseId = findViewById(R.id.tvCourseId);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvAttendanceSummary = findViewById(R.id.tvAttendanceSummary);
        btnDone.setBackgroundResource(R.color.colorRed_pressed);
        btnDone.setText(R.string.action_delete);

        attendanceItemRepository = new AttendanceItemRepository(getApplication());
        lectureRepository = new LectureRepository(getApplication());

        //get course data from last intent
        Intent intent = getIntent();
        lectureId = intent.getLongExtra(EasyAttendanceConstants.LECTURE_ID, 0);
        courseId = intent.getStringExtra(EasyAttendanceConstants.COURSE_ID);
        courseName = intent.getStringExtra(EasyAttendanceConstants.COURSE_NAME);
        studentAccountRegister = intent.getIntExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, 0);
        studentAcountChecked = intent.getIntExtra(EasyAttendanceConstants.ATTENDANCE_COUNT, 0);


        // get student checking information from database
        try {
            attendanceItemList = (ArrayList) attendanceItemRepository.getAttendancesWithLectureId(lectureId);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //set the view
        tvCourseId.setText(courseId);
        tvCourseName.setText(courseName);
        tvStudentCount.setText(String.format(getString(R.string.formatString_students_registered), studentAccountRegister));
        tvAttendanceSummary.setText(String.format(getString(R.string.formatString_in_attendance), studentAcountChecked, studentAccountRegister));
        attendanceAdapter = new AttendanceAdapter(this, R.layout.attendance_list_item, attendanceItemList);
        lvAttendanceList.setAdapter(attendanceAdapter);


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete the record in the database
                // delete with LectureRepository's delete method, it deletes with cascade, so both lecture and attendances are deleted.

                Boolean success = lectureRepository.deleteByLectureId(lectureId);
                if (success) {
                    Toast.makeText(getApplicationContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
