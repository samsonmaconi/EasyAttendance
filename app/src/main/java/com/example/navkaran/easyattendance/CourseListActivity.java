package com.example.navkaran.easyattendance;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

// David Cui B00788648 Nov 2018

/**
 * activity for course list, here instructors can select the course they want to enable check-in for,
 * and add/edit/delete courses
 */
public class CourseListActivity extends AppCompatActivity {

    //repository is the interface to local database operations
    //LiveData provides real time access to the courses in local database
    private CourseItemRepository repository;
    private LiveData<List<CourseItem>> courses;

    //UI element
    private ListView lvCourseList;
    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        getSupportActionBar().setTitle(R.string.title_course_list);

        repository = new CourseItemRepository(getApplication());
        courses = repository.getCourses();

        lvCourseList = findViewById(R.id.lvCourses);
        adapter = new CourseAdapter(this, R.layout.course_list_item, courses.getValue());
        lvCourseList.setAdapter(adapter);
        // this allows popup context menu when long clicking an item
        registerForContextMenu(lvCourseList);

        // setup observer for the LiveData, now whenever the data in database changes,
        // the updated course list will be given to adapter to update the view
        courses.observe(this, new Observer<List<CourseItem>>() {
            @Override
            public void onChanged(@Nullable List<CourseItem> courseItems) {
                adapter.setCourseList(courseItems);
            }
        });

        // floating action button for adding a course, I use startActivityForResult to collect
        // user inputs and afterwards insert course item to database in this activity.
        FloatingActionButton fab = findViewById(R.id.fabNewCourse);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseListActivity.this, NewCourseActivity.class);
                startActivityForResult(intent, EasyAttendanceConstants.NEW_COURSE_ACTIVITY_REQUEST_CODE);
            }
        });

        // when a course item in list view is clicked, go to TakeAttendanceActivity
        // i is element position in listview, listview elements have same order as the course list
        // therefore, get the clicked course with i from the course list
        lvCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CourseListActivity.this, TakeAttendanceActivity.class);
                CourseItem course = adapter.getCourseList().get(i);
                intent.putExtra(EasyAttendanceConstants.COURSE_KEY, course.getCourseKey());
                intent.putExtra(EasyAttendanceConstants.COURSE_ID, course.getCourseID());
                intent.putExtra(EasyAttendanceConstants.COURSE_NAME, course.getCourseName());
                intent.putExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, course.getStudentCount());
                startActivity(intent);
            }
        });
    }

    // when long clicking an item, context menu pops up, here I populate context menu with
    // "Edit" "Delete" "History"
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvCourses) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(adapter.getCourseList().get(info.position).getCourseID());
            String[] menuItems = getResources().getStringArray(R.array.stringArray_course_list_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    // when a context menu item is selected, handles the event
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // menuItemIndex is which menu item is selected, courseSelected is the course long clicked
        int menuItemIndex = item.getItemId();
        final CourseItem courseSelected = adapter.getCourseList().get(info.position);

        switch (menuItemIndex) {
            case 0: //Edit MenuItem, go to EditCourseActivity
                Intent intent = new Intent(CourseListActivity.this, EditCourseActivity.class);
                intent.putExtra(EasyAttendanceConstants.COURSE_KEY, courseSelected.getCourseKey());
                intent.putExtra(EasyAttendanceConstants.COURSE_ID, courseSelected.getCourseID());
                intent.putExtra(EasyAttendanceConstants.COURSE_NAME, courseSelected.getCourseName());
                intent.putExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, courseSelected.getStudentCount());
                startActivityForResult(intent, EasyAttendanceConstants.EDIT_COURSE_ACTIVITY_REQUEST_CODE);
                break;
            case 1: //Delete MenuItem, popup a confirmation dialog to prevent error
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.action_delete) + " " + courseSelected.getCourseID())
                        .setMessage(String.format(getString(R.string.formatString_confirm_delete), courseSelected.getCourseID()))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                repository.delete(courseSelected);
                                VibratorUtility.vibrate(getApplicationContext(), true);
                                Toast.makeText(getApplicationContext(), getString(R.string.alert_course_deleted), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case 2: //History MenuItem, go to AttendanceHistoryActivity
                Intent histIntent = new Intent(CourseListActivity.this, AttendanceHistoryActivity.class);
                histIntent.putExtra(EasyAttendanceConstants.COURSE_KEY, courseSelected.getCourseKey());
                histIntent.putExtra(EasyAttendanceConstants.COURSE_ID, courseSelected.getCourseID());
                histIntent.putExtra(EasyAttendanceConstants.COURSE_NAME, courseSelected.getCourseName());
                histIntent.putExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, courseSelected.getStudentCount());
                startActivity(histIntent);
                break;
        }
        return true;
    }

    // NewCourseActivity or EditCourseActivity are called with  startActivityForResult, so the result
    // is handled here
    // depending on requestCode (New or Edit) and resultCode (OK or CANCELED), handles differently
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EasyAttendanceConstants.NEW_COURSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // insert course into local db
            String courseId = data.getStringExtra(EasyAttendanceConstants.COURSE_ID);
            String courseName = data.getStringExtra(EasyAttendanceConstants.COURSE_NAME);
            int courseStudentCount = data.getIntExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, 0);
            repository.insert(new CourseItem(courseId, courseName, courseStudentCount));
            Toast.makeText(getApplicationContext(), getString(R.string.alert_new_course_added), Toast.LENGTH_SHORT).show();
            VibratorUtility.vibrate(getApplicationContext(), true);
        } else if (requestCode == EasyAttendanceConstants.EDIT_COURSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // update course edited
            int courseKey = data.getIntExtra(EasyAttendanceConstants.COURSE_KEY, -1);
            String courseId = data.getStringExtra(EasyAttendanceConstants.COURSE_ID);
            String courseName = data.getStringExtra(EasyAttendanceConstants.COURSE_NAME);
            int courseStudentCount = data.getIntExtra(EasyAttendanceConstants.COURSE_STUDENT_COUNT, 0);
            repository.update(new CourseItem(courseKey, courseId, courseName, courseStudentCount));
            Toast.makeText(getApplicationContext(), getString(R.string.alert_course_updated), Toast.LENGTH_SHORT).show();
            VibratorUtility.vibrate(getApplicationContext(), true);
        } else {
            // result CANCELED is caused by canceled form or incomplete form
            Toast.makeText(getApplicationContext(), getString(R.string.alert_canceled_or_incomplete_form), Toast.LENGTH_LONG).show();
            VibratorUtility.vibrate(getApplicationContext(), false);
        }
    }

}
