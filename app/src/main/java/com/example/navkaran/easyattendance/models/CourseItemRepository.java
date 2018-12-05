package com.example.navkaran.easyattendance.models;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

// David Cui B00788648 Nov 2018

/**
 * handles data operations related to CourseItem.
 * provides clean API to other parts of the app.
 * operation with the room database must happen on worker threads to prevent locking main UI thread,
 * this is achieved with AsyncTask.
 */
public class CourseItemRepository {

    private CourseItemDAO courseDAO;
    private LiveData<List<CourseItem>> courses;

    // lets repository access the database and initializes courses LiveData
    // whenever courses table is updated, the observer will notify the app in CourseListActivity
    // with the observer of the LiveData
    public CourseItemRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        courseDAO = db.courseItemDAO();
        courses = courseDAO.getAllCourses();
    }

    public LiveData<List<CourseItem>> getCourses() {
        return courses;
    }

    // database operation must happen on worker threads, done with AsyncTask
    public void insert (CourseItem course) {
        new InsertAsyncTask(courseDAO).execute(course);
    }

    public void update (CourseItem course) {
        new UpdateAsyncTask(courseDAO).execute(course);
    }

    public void delete (CourseItem course) {
        new DeleteAsyncTask(courseDAO).execute(course);
    }

    // AsyncTask that does insert operations on another thread
    private static class InsertAsyncTask extends AsyncTask<CourseItem, Void, Void> {

        private CourseItemDAO asyncTaskDAO;

        InsertAsyncTask(CourseItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Void doInBackground(final CourseItem... courses) {
            asyncTaskDAO.insertCourse(courses[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<CourseItem, Void, Void> {

        private CourseItemDAO asyncTaskDAO;

        UpdateAsyncTask(CourseItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Void doInBackground(final CourseItem... courses) {
            asyncTaskDAO.updateCourse(courses[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<CourseItem, Void, Void> {

        private CourseItemDAO asyncTaskDAO;

        DeleteAsyncTask(CourseItemDAO dao) {
            asyncTaskDAO = dao;
        }

        @Override
        protected  Void doInBackground(final CourseItem... courses) {
            asyncTaskDAO.deleteCourse(courses[0]);
            return null;
        }
    }

}
