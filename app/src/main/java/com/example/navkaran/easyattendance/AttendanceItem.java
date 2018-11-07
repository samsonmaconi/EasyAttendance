package com.example.navkaran.easyattendance;

public class AttendanceItem{
    private String studentId;
    private Boolean hasCheckedIn;
    private static final String[] status = {"Not Checked-in", "Checked-in"};

    public AttendanceItem(String studentId, Boolean hasCheckedIn) {
        this.studentId = studentId;
        this.hasCheckedIn = hasCheckedIn;
    }

    public String getStatus() {
        return this.hasCheckedIn ? status[1] : status[0];
    }

    public String getStudentId() {
        return studentId;
    }

    public Boolean hasCheckedIn() {
        return hasCheckedIn;
    }
}
