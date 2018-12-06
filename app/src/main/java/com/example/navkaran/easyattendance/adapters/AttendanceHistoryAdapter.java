package com.example.navkaran.easyattendance.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.navkaran.easyattendance.models.Lecture;
import com.example.navkaran.easyattendance.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


/**
 * @authors David Cui and Navkaran Kumar
 * Nov 2018
 * adapter for list of lectures in attendance history.
 * takes care of populating list view with contents of a list.
 */

public class AttendanceHistoryAdapter extends ArrayAdapter<Lecture> {
    private List<Lecture> lectureList;
    private SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd", Locale.getDefault());

    public AttendanceHistoryAdapter(Context context, int resource, List<Lecture> lectureList) {
        super(context, resource, lectureList);
        this.lectureList = lectureList;
    }

    // update list content and notify adapter to update view
    public void setLectures(List<Lecture> lectures) {
        lectureList = lectures;
        notifyDataSetChanged();
    }

    public List<Lecture> getLectureList() {
        return lectureList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // check to see if view is null. If it is, inflate it
        // inflating means to show the view
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.attendance_history_list_item, null);
        }

        // avoid null pointer
        Lecture i = null;
        if(lectureList != null) {
            i = lectureList.get(position);
        }

        // set the list item view with content
        if (i != null) {
            TextView tvAttenDate = v.findViewById(R.id.tvAttenDate);
            TextView tvAttenCount = v.findViewById(R.id.tvAttenCount);

            if(position % 2 == 0) {
                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            } else {
                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhiteFaded));
            }

            tvAttenDate.setText(df.format(i.getDate()));
            String formatStr = getContext().getResources().getString(R.string.formatString_students_checked_in);
            tvAttenCount.setText(String.format(formatStr, i.getNumAttendee()));
        }
        return v;
    }

    // adpater uses this to avoid null pointer
    @Override
    public int getCount() {
        if (lectureList != null)
            return lectureList.size();
        else return 0;
    }

}
