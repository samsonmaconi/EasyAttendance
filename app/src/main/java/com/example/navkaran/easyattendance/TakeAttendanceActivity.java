package com.example.navkaran.easyattendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by xiaoyutian on 2018-11-06.
 */

public class TakeAttendanceActivity extends AppCompatActivity {

    Button stop_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_class_attendance);
        getSupportActionBar().setTitle("Class Attendance");

        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(stop);
    }

    View.OnClickListener stop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startNewIntend();
        }
    };

    private void startNewIntend(){
        Intent intent = new Intent(this, AttendanceDetailsActivity.class);
        //intent.putExtra("LONGITUDE", longitude);
        //intent.putExtra("LATITUDE", latitude);
        startActivity(intent);
    }

}
