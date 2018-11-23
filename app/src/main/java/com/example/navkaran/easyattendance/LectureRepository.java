package com.example.navkaran.easyattendance;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class LectureRepository {

    private LectureDAO lectureDAO;

    public LectureRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        lectureDAO = db.lectureDAO();
    }

    // method wrapper
    public List<Lecture> getLecturesByCourseKey(int courseKey) throws Exception {
        return new GetLecturesAsyncTask(lectureDAO).execute(courseKey).get();
    }

    public long insert (Lecture lecture) throws Exception {
        return new InsertAsyncTask(lectureDAO).execute(lecture).get();
    }

    public boolean deleteByLectureId (long lectureId) {
        try {
            new DeleteAsyncTask(lectureDAO).execute(lectureId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // async tasks
    private static class GetLecturesAsyncTask extends AsyncTask<Integer, Void, List<Lecture>> {

        private LectureDAO asyncTaskDAO;

        GetLecturesAsyncTask(LectureDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected List<Lecture> doInBackground(final Integer... courseKey) {
            return asyncTaskDAO.getLecturesByCourseKey(courseKey[0]);
        }
    }

    private static class InsertAsyncTask extends AsyncTask<Lecture, Void, Long> {

        private LectureDAO asyncTaskDAO;

        InsertAsyncTask(LectureDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Long doInBackground(final Lecture... lecture) {
            return asyncTaskDAO.insertLecture(lecture[0]);
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Long, Void, Void> {

        private LectureDAO asyncTaskDAO;

        DeleteAsyncTask(LectureDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected Void doInBackground(final Long... lectureId) {
            asyncTaskDAO.deleteByLectureId(lectureId[0]);
            return null;
        }
    }
}
