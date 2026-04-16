package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /teachers/{teacherId}/classes
 * Assigns a teacher to a course+semester.
 */
public class AssignClassRequest {

    @NotBlank(message = "Course cannot be empty")
    private String course;

    @Min(1) @Max(8)
    private int semester;

    public AssignClassRequest() {}

    public String getCourse()   { return course; }
    public int    getSemester() { return semester; }

    public void setCourse(String course)     { this.course = course; }
    public void setSemester(int semester)    { this.semester = semester; }
}
