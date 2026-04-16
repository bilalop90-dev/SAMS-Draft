package com.example.demo.dto;

public class StudentAttention {
    private String studentId;
    private String studentName;
    private int totalClasses;
    private int attendedClasses;
    private double percentage;
    private String severity;

    public StudentAttention(String studentId, String studentName, int totalClasses, int attendedClasses, double percentage, String severity) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
        this.percentage = percentage;
        this.severity = severity;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getSeverity() {
        return severity;
    }
}

