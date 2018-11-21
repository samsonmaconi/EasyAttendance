package com.example.navkaran.easyattendance;

import android.app.Application;

public class AttendanceItemRepository {

    private AttendanceItemDAO attendanceItemDAO;

    public AttendanceItemRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        AttendanceItemDAO attendanceItemDAO = db.attendanceItemDAO();

    }

    // method wrapper


    // async tasks
}
