package com.example.demo.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "studentAttendance")
public class Attendance {

    @Id
    private String id;

    @NotBlank(message = "Student name cannot be empty")
    private String studentName;

    @NotBlank(message = "Student ID cannot be empty")
    private String studentId;

    @NotBlank(message = "Date cannot be empty")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Date must be in format YYYY-MM-DD"
    )
    private String date;

    @NotBlank(message = "Status cannot be empty")
    @Pattern(
            regexp = "^(Present|Absent)$",
            message = "Status must be exactly 'Present' or 'Absent'"
    )
    private String status;

    @NotBlank(message = "Teacher name cannot be empty")
    private String teacherName;

    private String teacherId;
    private String course;
    private Integer semester;

    public Attendance() {}

    /** Legacy constructor — keeps existing code compiling */
    public Attendance(String id, String studentId, String studentName,
                      String date, String status, String teacherName) {
        this.id          = id;
        this.studentId   = studentId;
        this.studentName = studentName;
        this.date        = date;
        this.status      = status;
        this.teacherName = teacherName;
    }

    /** Full constructor with class context */
    public Attendance(String id, String studentId, String studentName,
                      String date, String status, String teacherName,
                      String course, Integer semester) {
        this(id, studentId, studentName, date, status, teacherName);
        this.course   = course;
        this.semester = semester;
    }

    public String  getId()          { return id; }
    public String  getStudentId()   { return studentId; }
    public String  getStudentName() { return studentName; }
    public String  getDate()        { return date; }
    public String  getStatus()      { return status; }
    public String  getTeacherName() { return teacherName; }
    public String  getCourse()      { return course; }
    public Integer getSemester()    { return semester; }

    public void setId(String id)                   { this.id = id; }
    public void setStudentId(String studentId)     { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setDate(String date)               { this.date = date; }
    public void setStatus(String status)           { this.status = status; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public void setCourse(String course)           { this.course = course; }
    public void setSemester(Integer semester)      { this.semester = semester; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(studentId, that.studentId)
                && Objects.equals(date, that.date)
                && Objects.equals(status, that.status)
                && Objects.equals(teacherName, that.teacherName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, date, status, teacherName);
    }
}