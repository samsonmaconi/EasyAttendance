package com.example.navkaran.easyattendance;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class LectureRepository {

    private LectureDAO lectureDAO;

    public LectureRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        lectureDAO = db.lectureDAO();
    }

    public LiveData<List<Lecture>> getLecturesWithCourseKey(int courseKey) {
        return lectureDAO.getLecturesSync(courseKey);
    }

    // method wrapper
    public long insert (Lecture lecture) throws Exception {
        return new InsertAsyncTask(lectureDAO).execute(lecture).get();
    }

    // async tasks
    private static class InsertAsyncTask extends AsyncTask<Lecture, Void, Long> {

        private LectureDAO asyncTaskDAO;

        InsertAsyncTask(LectureDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Long doInBackground(final Lecture... params) {
            return asyncTaskDAO.insertLecture(params[0]);
        }
    }
}
