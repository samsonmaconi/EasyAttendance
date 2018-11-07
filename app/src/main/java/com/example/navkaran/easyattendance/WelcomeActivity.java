package com.example.navkaran.easyattendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class WelcomeActivity extends AppCompatActivity {

    public EditText et_userId;
    public Button bt_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        et_userId = findViewById(R.id.et_userId);
        bt_save = findViewById(R.id.bt_save);
    }
}
