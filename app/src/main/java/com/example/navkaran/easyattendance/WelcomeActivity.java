package com.example.navkaran.easyattendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    public TextView tv_userId;
    public EditText et_userId;
    public Button bt_save;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tv_userId = findViewById(R.id.tv_userId);
        et_userId = findViewById(R.id.et_userId);
        bt_save = findViewById(R.id.bt_save);
        sp = this.getSharedPreferences("id_data", 0);
        editor = sp.edit();

        //get the data from select_uer_type Activity
        final Intent intent = getIntent();
        final String role = intent.getStringExtra("role");
        if (role.equals("student")) {
            tv_userId.setText("Student ID");
        } else if (role.equals("staff")) {
            tv_userId.setText("Staff ID");
        }

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // save the data for studentID/staffID
                String id = et_userId.getText().toString();
                editor.putString("id", id);
                editor.commit();
                Toast.makeText(getApplicationContext(), "save successfully", Toast.LENGTH_SHORT).show();

                //close the current activity and open next activity
                Intent intent1 = new Intent();
                intent1.setClass(getApplicationContext(),CheckAttendanceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("role",role);
                bundle.putString("id",id);
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });
    }
}
