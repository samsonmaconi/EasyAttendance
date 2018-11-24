package com.example.navkaran.easyattendance;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class AttendanceHistoryAdapter extends ArrayAdapter<Lecture> {
    private List<Lecture> lectureList;
    private SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd");

    public AttendanceHistoryAdapter(Context context, int resource, List<Lecture> LectureList) {
        super(context, resource, LectureList);
        this.lectureList = lectureList;
    }

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

        Lecture i = null;
        if(lectureList != null) {
            i = lectureList.get(position);
        }

        if (i != null) {
            TextView attenDate = v.findViewById(R.id.attendate);
            TextView attenCount = v.findViewById(R.id.attenCount);

            if(position % 2 == 0) {
                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            } else {
                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhiteFaded));
            }

            attenDate.setText(df.format(i.getDate()));
            String formatStr = getContext().getResources().getString(R.string.formatString_students_registered);
            attenCount.setText(String.format(formatStr, i.getNumAttendee()));
        }
        return v;
    }

    @Override
    public int getCount() {
        if (lectureList != null)
            return lectureList.size();
        else return 0;
    }

}
