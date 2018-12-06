package com.example.navkaran.easyattendance.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.navkaran.easyattendance.utils.EasyAttendanceConstants;
import com.example.navkaran.easyattendance.R;

// David Cui Nov 2018

/**
 * gets input from users required for adding a course to the list
 */
public class NewCourseActivity extends AppCompatActivity {

    private EditText etCourseId;
    private EditText etCourseName;
    private EditText etCourseStudentCount;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        getSupportActionBar().setTitle(R.string.title_add_course);

        etCourseId = findViewById(R.id.etCourseId);
        etCourseName = findViewById(R.id.etCourseName);
        etCourseStudentCount = findViewById(R.id.etCourseStudentCount);
        btnSave = findViewById(R.id.btnSave);

        // gets the input and return them to the caller activity - CourseListActivity
        // the result code is CANCELED is the user cancels the activity with the back hardware
        // button or provides incomplete form
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
