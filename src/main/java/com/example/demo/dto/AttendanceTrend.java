package com.example.demo.dto;

public class AttendanceTrend {
    private String studentId;
    private String studentName;
    private double recentPercentage;
    private double previousPercentage;
    private double change;
    private String trend; // IMPROVING, DECLINING, STABLE

    public AttendanceTrend(String studentId, String studentName,
                           double recentPercentage, double previousPercentage,
                           double change, String trend) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.recentPercentage = recentPercentage;
        this.previousPercentage = previousPercentage;
        this.change = change;
        this.trend = trend;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public double getRecentPercentage() { return recentPercentage; }
    public double getPreviousPercentage() { return previousPercentage; }
    public double getChange() { return change; }
    public String getTrend() { return trend; }
}