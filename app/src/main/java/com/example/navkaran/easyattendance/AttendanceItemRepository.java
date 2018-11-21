package com.example.navkaran.easyattendance;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class AttendanceItemRepository {

    private AttendanceItemDAO attendanceItemDAO;

    public AttendanceItemRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        AttendanceItemDAO attendanceItemDAO = db.attendanceItemDAO();
    }

    public List<AttendanceItem> getAttendancesWithLectureId(long lectureId) {
        return attendanceItemDAO.getAttendances(lectureId).getValue();
    }

    public void insert (AttendanceItem attendance) {
        new InsertAsyncTask(attendanceItemDAO).execute(attendance);
    }

    private static class InsertAsyncTask extends AsyncTask<AttendanceItem, Void, Void> {

        private AttendanceItemDAO asyncTaskDAO;

        InsertAsyncTask(AttendanceItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Void doInBackground(final AttendanceItem... params) {
            asyncTaskDAO.insertAttendance(params[0]);
            return null;
        }
    }
}
