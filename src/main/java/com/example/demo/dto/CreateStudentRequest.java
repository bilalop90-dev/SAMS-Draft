package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateStudentRequest {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Student ID cannot be empty")
    private String studentId; // must match rollNumber in students collection

    public CreateStudentRequest() {}

    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getName()      { return name; }
    public String getStudentId() { return studentId; }

    public void setUsername(String username)   { this.username = username; }
    public void setPassword(String password)   { this.password = password; }
    public void setName(String name)           { this.name = name; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}