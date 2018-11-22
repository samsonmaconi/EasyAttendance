package com.example.navkaran.easyattendance;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class AttendanceItemRepository {

    private AttendanceItemDAO attendanceItemDAO;

    public AttendanceItemRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        attendanceItemDAO = db.attendanceItemDAO();
    }

    public LiveData<List<AttendanceItem>> getAttendancesWithLectureId(long lectureId) {
        return attendanceItemDAO.getAttendances(lectureId);
    }

    public void insert (AttendanceItem[] attendances) {
        new InsertAsyncTask(attendanceItemDAO).execute(attendances);
    }

    private static class InsertAsyncTask extends AsyncTask<AttendanceItem[], Void, Void> {

        private AttendanceItemDAO asyncTaskDAO;

        InsertAsyncTask(AttendanceItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Void doInBackground(final AttendanceItem[]... params) {
            asyncTaskDAO.insertAttendances(params[0]);
            return null;
        }
    }
}
