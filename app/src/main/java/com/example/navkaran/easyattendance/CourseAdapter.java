package com.example.navkaran.easyattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CourseAdapter extends ArrayAdapter<CourseItem> {

    private ArrayList<CourseItem> courseList;

    public CourseAdapter(Context context, int textViewResourceId, ArrayList<CourseItem> courseList) {
        super(context, textViewResourceId, courseList);
        this.courseList = courseList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.course_list_item, null);
        }

        CourseItem i = courseList.get(position);

        if (i != null) {
            TextView tvCourseId = v.findViewById(R.id.tvCourseId);
            TextView tvCourseName = v.findViewById(R.id.tvCourseName);
            TextView tvStudentCount = v.findViewById(R.id.tvStudentCount);

            tvCourseId.setText(i.getCourseID());
            tvCourseName.setText(i.getCourseName());
            tvStudentCount.setText(Integer.toString(i.getStudentCount()) + " Students");
        }

        if(position % 2 == 0) {
            v.setBackgroundColor(v.getResources().getColor(R.color.colorNothing));
        } else {
            v.setBackgroundColor(v.getResources().getColor(R.color.colorWhite));
        }

        return v;
    }

}