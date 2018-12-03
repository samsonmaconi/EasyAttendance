package com.example.navkaran.easyattendance;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * A class for student attendance details
 */
@Entity(tableName = "attendances",
        primaryKeys = {"student_id", "lecture_id"},
        foreignKeys = {
                @ForeignKey(onDelete = CASCADE,
                        entity = Lecture.class,
                        parentColumns = "lecture_id",
                        childColumns = "lecture_id")},
        indices = {@Index(value = "lecture_id")})
public class AttendanceItem {
    @ColumnInfo(name = "student_id")
    @NonNull
    private String studentId;
    @ColumnInfo(name = "lecture_id")
    private long lectureId;
    @Ignore
    private Boolean hasCheckedIn = true;
    @Ignore
    private static final String[] status = {"Not Checked-in", "Checked-in"};

    @Ignore
    public AttendanceItem(@NonNull String studentId, Boolean hasCheckedIn) {
        this.studentId = studentId;
        this.hasCheckedIn = hasCheckedIn;
    }

    public AttendanceItem(@NonNull String studentId, long lectureId) {
        this.studentId = studentId;
        this.lectureId = lectureId;
    }

    public long getLectureId() {
        return lectureId;
    }

    public void setLectureId(long lectureId) {
        this.lectureId = lectureId;
    }

    public String getStatus() {
        return this.hasCheckedIn ? status[1] : status[0];
    }

    public @NonNull
    String getStudentId() {
        return studentId;
    }

    public Boolean hasCheckedIn() {
        return hasCheckedIn;
    }
}
