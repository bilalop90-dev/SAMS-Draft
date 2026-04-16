package com.example.demo.dto;

public class StudentPerformance {
    private String studentId;
    private String studentName;
    private double percentage;

    public StudentPerformance(String studentId, String studentName, double percentage) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.percentage = percentage;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public double getPercentage() {
        return percentage;
    }
}
