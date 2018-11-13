package com.example.navkaran.easyattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    private Intent intent;
    private String userRole, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Retrieve the user role and ID from shared preferences. If there's no info, returns "none"
        userRole = sharedPreferences.getString("userRole", "none");
        userID = sharedPreferences.getString("userID", "none");
        
        switch (userRole){
            case "teacher":
                intent = new Intent(this, CourseListActivity.class);
                intent.putExtra("userRole", userRole);
                intent.putExtra("userID", userID);
                startActivity(intent);
            case "student":
                intent = new Intent(this, CheckAttendanceActivity.class);
                intent.putExtra("userRole", userRole);
                intent.putExtra("userID", userID);
                startActivity(intent);
            case "none":
                intent = new Intent(this, SelectUserType.class);
                startActivity(intent);
            default:
                break;
        }

    }
}
