package com.example.navkaran.easyattendance;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class LectureRepository {

    private LectureDAO lectureDAO;

    public LectureRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        LectureDAO lectureDAO = db.lectureDAO();
    }

    public List<Lecture> getLecturesWithCourseKey(int courseKey) {
        return lectureDAO.getLecturesSync(courseKey).getValue();
    }

    // method wrapper
    public long insert (Lecture lecture) throws Exception {
        Long id =  new InsertAsyncTask(lectureDAO).execute(lecture).get();
        return id;
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
