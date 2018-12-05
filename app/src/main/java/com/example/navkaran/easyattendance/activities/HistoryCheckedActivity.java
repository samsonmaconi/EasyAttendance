package com.example.navkaran.easyattendance.activities;


// Author: Lan Chen, B00809814
// activity for checking history attendence for specific lecture
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navkaran.easyattendance.adapters.AttendanceAdapter;
import com.example.navkaran.easyattendance.models.AttendanceItem;
import com.example.navkaran.easyattendance.models.AttendanceItemRepository;
import com.example.navkaran.easyattendance.utils.EasyAttendanceConstants;
import com.example.navkaran.easyattendance.models.LectureRepository;
import com.example.navkaran.easyattendance.R;
import com.example.navkaran.easyattendance.utils.VibratorUtility;

import java.util.ArrayList;

public class HistoryCheckedActivity extends AppCompatActivity {

    private ListView lvAttendanceList;
    private Button btnDone;
    private TextView tvCourseId, tvCourseName, tvStudentCount, tvAttendanceSummary;
    private long lectureId;
    private String courseId;
    private String courseName;
    private int studentAccountRegister;
    private int studentAcountChecked;

    private Context context;

    private ArrayList<AttendanceItem> attendanceItemList;
    private AttendanceAdapter attendanceAdapter;

    private AttendanceItemRepository attendanceItemRepository;
    private LectureRepository lectureRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        getSupportActionBar().setTitle(R.string.title_attendance_list_hist);
        context = this;

        lvAttendanceList = findViewById(R.id.lvAttendanceList);
        btnDone = findViewById(R.id.btnDone);
        tvCourseId = findViewById(R.id.tvCourseId);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvAttendanceSummary = findViewById(R.id.tvAttendanceSummary);
        btnDone.setBackgroundResource(R.drawable.rounded_rect_button_red_selector);
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

                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.action_delete))
                        .setMessage(String.format(getString(R.string.formatString_confirm_delete_lecture), courseId))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Delete the record in the database
                                // delete with LectureRepository's delete method, it deletes with cascade, so both lecture and attendances are deleted.
                                Boolean success = lectureRepository.deleteByLectureId(lectureId);
                                if (success) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.alert_course_deleted), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.toast_historychecked_error), Toast.LENGTH_SHORT).show();
                                }
                                VibratorUtility.vibrate(getApplicationContext(), true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }
}
