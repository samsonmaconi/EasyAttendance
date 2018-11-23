package com.example.navkaran.easyattendance;
/**
 * This class retrieves the users role and ID from shared preferences.
 * It then redirect's the user to the relevant activity based on the role.
 * If there is no corresponding data saved (first time user), the user
 * is redirected to the initial setup view
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    private Intent intent;
    private String userRole, userID;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("CONTAINER",Context.MODE_PRIVATE);
        userRole = sp.getString("userRole", "none");
        userID = sp.getString("userID", "none");

        Log.d(TAG,"Role, ID: " + userRole + ", " +userID);

        checkUserRole();
    }

    private void checkUserRole(){
        if(userRole.equals("teacher")){
            intent = new Intent(this, CourseListActivity.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userID", userID);
            startActivity(intent);
            finish();
        }else if(userRole.equals("student")){
            intent = new Intent(this, CheckAttendanceActivity.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userID", userID);
            startActivity(intent);
            finish();
        }else{
            intent = new Intent(this, SelectUserType.class);
            startActivity(intent);
            finish();
        }
    }
}
