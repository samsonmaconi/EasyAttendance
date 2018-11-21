package com.example.navkaran.easyattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewCourseActivity extends AppCompatActivity {

    private EditText etCourseId;
    private EditText etCourseName;
    private EditText etCourseStudentCount;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        etCourseId = findViewById(R.id.et_courseId);
        etCourseName = findViewById(R.id.et_courseName);
        etCourseStudentCount = findViewById(R.id.et_courseStudentCount);
        btnSave = findViewById(R.id.btn_save);

        // gets the input and return them to the caller activity - CourseListActivity
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();

                if (TextUtils.isEmpty(etCourseId.getText())
                        || TextUtils.isEmpty(etCourseName.getText())
                        || TextUtils.isEmpty(etCourseStudentCount.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String courseId = etCourseId.getText().toString();
                    String courseName = etCourseName.getText().toString();
                    int studentCount = Integer.parseInt(etCourseStudentCount.getText().toString());
                    replyIntent.putExtra(EasyAttendanceConstants.COURSE_ID, courseId);
                    replyIntent.putExtra(EasyAttendanceConstants.COURSE_NAME, courseName);
                    replyIntent.putExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, studentCount);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }

}
