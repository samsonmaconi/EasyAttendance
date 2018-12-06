package com.example.navkaran.easyattendance.models;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

// David Cui Nov 2018

/**
 * Repository is a layer on top of DAO, they get DAO from the database instance.
 * Then, uses DAO to do the actual CRUD operations.
 * Since SQL operations needs to be performed on worker threads, there are AsyncTasks
 * for each SQL operation.
 */
public class AttendanceItemRepository {

    private AttendanceItemDAO attendanceItemDAO;

    // get database instance and get DAO from database
    // parameter: application context
    public AttendanceItemRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        attendanceItemDAO = db.attendanceItemDAO();
    }

    // get all attendances for a particular lecture
    // parameter: ID of lecture
    public List<AttendanceItem> getAttendancesWithLectureId(long lectureId) throws Exception {
        return new GetAttendancesAsyncTask(attendanceItemDAO).execute(lectureId).get();
    }

    // get all attendances for a lecture with the background thread AsyncTask
    private static class GetAttendancesAsyncTask extends AsyncTask<Long, Void, List<AttendanceItem>> {

        private AttendanceItemDAO asyncTaskDAO;

        GetAttendancesAsyncTask(AttendanceItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected List<AttendanceItem> doInBackground(final Long... lectureId) {
            return asyncTaskDAO.getAttendancesByLectureId(lectureId[0]);
        }
    }

    // gets an array of AttendanceItem because varargs is used for batch insertion
    // parameter: array of attendanceItem
    public void insert (AttendanceItem[] attendances) {
        new InsertAsyncTask(attendanceItemDAO).execute(attendances);
    }

    // does batch insert on the background thread AsyncTask
    private static class InsertAsyncTask extends AsyncTask<AttendanceItem[], Void, Void> {

        private AttendanceItemDAO asyncTaskDAO;

        InsertAsyncTask(AttendanceItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Void doInBackground(final AttendanceItem[]... attendanceArrays) {
            asyncTaskDAO.insertAttendances(attendanceArrays[0]);
            return null;
        }
    }
}
