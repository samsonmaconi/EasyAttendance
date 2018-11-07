package com.example.navkaran.easyattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AttendanceAdapter extends ArrayAdapter<AttendanceItem> {

    private ArrayList<AttendanceItem> attendanceList;

    public AttendanceAdapter(Context context, int resource, ArrayList<AttendanceItem> attendanceList) {
        super(context, resource, attendanceList);
        this.attendanceList = attendanceList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // check to see if view is null. If it is, inflate it
        // inflating means to show the view
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.attendance_list_item, null);
        }

        AttendanceItem i = attendanceList.get(position);

        if (i != null) {
            TextView tvStudentId = v.findViewById(R.id.tvStudentId);
            TextView tvStatus = v.findViewById(R.id.tvStatus);
            TextView tvSN = v.findViewById(R.id.tvSN);

            tvStudentId.setText(i.getStudentId());
            tvStatus.setText(i.getStatus());
            tvSN.setText(String.valueOf(position + 1));

            if(i.hasCheckedIn()){
                tvStatus.setTextColor(v.getResources().getColor(R.color.colorGreen));
            }else
                tvStatus.setTextColor(v.getResources().getColor(R.color.colorRed));
            }

        return v;
    }

}
