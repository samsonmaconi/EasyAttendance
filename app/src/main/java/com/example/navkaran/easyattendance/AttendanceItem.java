package com.example.navkaran.easyattendance;

public class AttendanceItem{
    private static int checkedInCount = 0;
    private static int totalCount = 0;
    private String studentId;
    private Boolean hasCheckedIn;
    private static final String[] status = {"Not Checked-in", "Checked-in"};

    public AttendanceItem(String studentId, Boolean hasCheckedIn) {
        this.studentId = studentId;
        this.hasCheckedIn = hasCheckedIn;
        if(hasCheckedIn){
            checkedInCount++;
        }
        totalCount++;
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

    public static int getCheckedInCount() {
        return checkedInCount;
    }

    public static int getTotalCount() {
        return totalCount;
    }
}
