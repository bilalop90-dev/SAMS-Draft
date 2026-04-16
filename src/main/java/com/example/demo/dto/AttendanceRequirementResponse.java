package com.example.demo.dto;

public class AttendanceRequirementResponse {
    private double currentPercentage;
    private int classesNeeded;

    public AttendanceRequirementResponse(double currentPercentage, int classesNeeded) {
        this.currentPercentage = currentPercentage;
        this.classesNeeded = classesNeeded;
    }

    public double getCurrentPercentage() {
        return currentPercentage;
    }

    public int getClassesNeeded() {
        return classesNeeded;
    }
}
