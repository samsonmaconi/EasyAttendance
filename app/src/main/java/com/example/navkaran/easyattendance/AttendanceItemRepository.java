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

    public List<AttendanceItem> getAttendancesWithLectureId(long lectureId) throws Exception {
        return new GetAttendancesAsyncTask(attendanceItemDAO).execute(lectureId).get();
    }

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
    public void insert (AttendanceItem[] attendances) {
        new InsertAsyncTask(attendanceItemDAO).execute(attendances);
    }

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
