package com.example.demo.dto;

public class TeacherDashboardResponse {

    private int totalStudents;
    private double classAveragePercentage;
    private int safeCount;
    private int warningCount;
    private int criticalCount;

    public TeacherDashboardResponse(int totalStudents, double classAveragePercentage, int safeCount, int warningCount, int criticalCount) {
        this.totalStudents = totalStudents;
        this.classAveragePercentage = classAveragePercentage;
        this.safeCount = safeCount;
        this.warningCount = warningCount;
        this.criticalCount = criticalCount;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public double getClassAveragePercentage() {
        return classAveragePercentage;
    }

    public int getSafeCount() {
        return safeCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public int getCriticalCount() {
        return criticalCount;
    }
}
