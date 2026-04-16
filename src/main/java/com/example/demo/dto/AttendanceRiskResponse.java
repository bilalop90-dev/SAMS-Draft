package com.example.demo.dto;

public class AttendanceRiskResponse {

    private double percentage;
    private String risk;

    public AttendanceRiskResponse(double percentage, String risk) {
        this.percentage = percentage;
        this.risk = risk;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getRisk() {
        return risk;
    }
}