package com.example.navkaran.easyattendance;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.navkaran.easyattendance.AttendanceDetailsActivity.ATTENDANCE_COUNT;

public class AttendanceHistory extends AppCompatActivity {
    private CourseItemRepository repository;
    private LiveData<List<CourseItem>> lectures;
    ListView datelist;
    private AttendanceHistoryAdapter adapter;
    private LectureRepository lectureRepository;
    private Intent intent;
    private String courseId,courseName;
    private TextView className,classNumber,registeredStudents;
    private int courseKey, studentCount, attendanceCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        className = findViewById(R.id.class_name);
        classNumber = findViewById(R.id.class_number);
        registeredStudents = findViewById(R.id.register_number);

        intent = getIntent();
        courseKey = intent.getIntExtra(EasyAttendanceConstants.COURSE_KEY, -1);

        courseId = intent.getStringExtra(EasyAttendanceConstants.COURSE_ID);
        classNumber.setText(String.valueOf(courseId));
        courseName = intent.getStringExtra(EasyAttendanceConstants.COURSE_NAME);
        className.setText(courseName);
        //TODO: Change Default Values back to 0
        studentCount = intent.getIntExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, 0);
        registeredStudents.setText(String.valueOf(studentCount));
        attendanceCount = intent.getIntExtra(ATTENDANCE_COUNT, 0);
        ArrayList<Lecture> Testing = new ArrayList<Lecture>();
        Testing.add(new Lecture(50,new Date(),courseKey));
        datelist = findViewById(R.id.lvdatelist);
        try {
            // Will be uncommented when database have entries in it

            //adapter= new AttendanceHistoryAdapter(this, R.layout.attendance_history_list_item,lectureRepository.getLecturesByCourseKey(courseKey));
            adapter = new AttendanceHistoryAdapter(this, R.layout.attendance_history_list_item,Testing);
        } catch (Exception e) {
            e.printStackTrace();
        }
        datelist.setAdapter(adapter);

        registerForContextMenu(datelist);



    }
}
