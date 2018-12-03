package com.example.navkaran.easyattendance;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// David Cui B00788648 Nov 2018

/**
 * adapter for course item list in course list activity.
 * takes care of populating list view with contents of a list.
 */
public class CourseAdapter extends ArrayAdapter<CourseItem> {

    private List<CourseItem> courseList;

    public CourseAdapter(Context context, int textViewResourceId, List<CourseItem> courseList) {
        super(context, textViewResourceId, courseList);
        this.courseList = courseList;
    }

    // this method changes the list content of the adapter, notifies the change to adapter,
    // so adapter will update the view
    // parameter: updated course list
    public void setCourseList(List<CourseItem> courses) {
        courseList = courses;
        notifyDataSetChanged();
    }

    public List<CourseItem> getCourseList() {
        return courseList;
    }

    //display each view in the list of course items
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        //if view is null, inflate/show/render it
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.course_list_item, null);
        }

        //get course i from the courseList and set textviews of the list item view with values
        // of course i, i is also position in list view, so we have same ordering
        CourseItem i = null;
        if(courseList != null) {
             i = courseList.get(position);
        }
        if (i != null) {
            TextView tvCourseId = v.findViewById(R.id.tvCourseId);
            TextView tvCourseName = v.findViewById(R.id.tvCourseName);
            TextView tvStudentCount = v.findViewById(R.id.tvStudentCount);

            // set textviews with appropriate values of course i
            tvCourseId.setText(i.getCourseID());
            tvCourseName.setText(i.getCourseName());
            tvStudentCount.setText(String.format(
                    getContext().getString(R.string.formatString_students_registered),
                    i.getStudentCount()));

            // alternate list item background color
            if(position % 2 == 0) {
                v.setBackgroundColor(getContext().getColor(R.color.colorWhite));
            } else {
                v.setBackgroundColor(getContext().getColor(R.color.colorWhiteFaded));
            }
        }
        return v;
    }

    // adapter uses this to avoid null pointer exception when list is null
    @Override
    public int getCount() {
        if (courseList != null)
            return courseList.size();
        else return 0;
    }

}