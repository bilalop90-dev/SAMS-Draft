package com.example.demo.dto;

public class TopPerformer {
    private int rank;
    private String studentId;
    private String studentName;
    private double percentage;
    private int totalClasses;
    private int attendedClasses;

    public TopPerformer(int rank, String studentId, String studentName,
                        double percentage, int totalClasses, int attendedClasses) {
        this.rank = rank;
        this.studentId = studentId;
        this.studentName = studentName;
        this.percentage = percentage;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
    }

    public int getRank() { return rank; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public double getPercentage() { return percentage; }
    public int getTotalClasses() { return totalClasses; }
    public int getAttendedClasses() { return attendedClasses; }
}