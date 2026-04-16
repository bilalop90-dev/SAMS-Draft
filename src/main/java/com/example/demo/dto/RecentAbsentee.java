package com.example.demo.dto;

public class RecentAbsentee {
    private String rollNumber;
    private String name;
    private int consecutiveAbsences;

    public RecentAbsentee(String rollNumber, String name, int consecutiveAbsences) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.consecutiveAbsences = consecutiveAbsences;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getName() {
        return name;
    }

    public int getConsecutiveAbsences() {
        return consecutiveAbsences;
    }
}

