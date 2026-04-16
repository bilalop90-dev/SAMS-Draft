package com.example.demo.dto;

public class LoginResponse {

    private String username;
    private String name;
    private String role;
    private String studentId; // null for teachers
    private String teacherId; // null for students — User.id for teachers

    public LoginResponse(String username, String name,
                         String role, String studentId) {
        this.username  = username;
        this.name      = name;
        this.role      = role;
        this.studentId = studentId;
        // teacherId not set in this legacy constructor — stays null
    }

    public LoginResponse(String username, String name,
                         String role, String studentId, String teacherId) {
        this.username  = username;
        this.name      = name;
        this.role      = role;
        this.studentId = studentId;
        this.teacherId = teacherId;
    }

    public String getUsername()  { return username; }
    public String getName()      { return name; }
    public String getRole()      { return role; }
    public String getStudentId() { return studentId; }
    public String getTeacherId() { return teacherId; }
}