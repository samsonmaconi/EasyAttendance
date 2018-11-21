package com.example.navkaran.easyattendance;

import android.app.Application;

public class LectureRepository {

    private LectureDAO lectureDAO;

    public LectureRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        LectureDAO lectureDAO = db.lectureDAO();

    }

    // method wrapper


    // async tasks
}
