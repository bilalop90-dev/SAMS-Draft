// Student Attendance Management System - Student Model
package com.example.demo.Model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "students")
public class Student {

    @Id
    private String id;

    @NotBlank(message = "Student name cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    // FIX 1 — @Indexed(unique = true) prevents two students sharing the same
    // rollNumber at the database level. Even if validation is bypassed, MongoDB
    // will reject the duplicate write.
    @Indexed(unique = true)
    @NotBlank(message = "Roll number cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Roll number must be alphanumeric only")
    private String rollNumber;

    // FIX 2 — course and semester are now @NotBlank / @NotNull so the API
    // rejects any request that omits them. className is kept as a derived,
    // read-only convenience field — it is never required in the request body.
    @NotBlank(message = "Course cannot be empty (e.g. BCA, MCA)")
    private String course;

    @NotNull(message = "Semester cannot be null")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 8, message = "Semester must be at most 8")
    private Integer semester;

    // Derived field — computed from course + semester, never sent by client.
    // Kept so that any code that still calls getClassName() continues to work.
    private String className;

    // ── Constructors ──────────────────────────────────────────────────────

    public Student() {}

    // Legacy constructor used by DataLoader — still compiles.
    // Parses "BCA Sem 1" style strings into course + semester.
    public Student(String id, String name, String rollNumber, String className) {
        this.id          = id;
        this.name        = name;
        this.rollNumber  = rollNumber;
        parseClassName(className);
        this.className   = className;
    }

    // New structured constructor used by updated DataLoader.
    public Student(String id, String name, String rollNumber,
                   String course, int semester) {
        this.id         = id;
        this.name       = name;
        this.rollNumber = rollNumber;
        this.course     = course;
        this.semester   = semester;
        this.className  = course + " Sem " + semester;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public String  getId()         { return id; }
    public String  getName()       { return name; }
    public String  getRollNumber() { return rollNumber; }
    public String  getCourse()     { return course; }
    public Integer getSemester()   { return semester; }

    // className is always derived — returns the computed value
    public String  getClassName()  { return className; }

    // ── Setters ───────────────────────────────────────────────────────────

    public void setId(String id)              { this.id = id; }
    public void setName(String name)          { this.name = name; }
    public void setRollNumber(String r)       { this.rollNumber = r; }

    public void setCourse(String course) {
        this.course = course;
        syncClassName();
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
        syncClassName();
    }

    // Kept so that StudentController.updateStudent() still compiles when it
    // calls existing.setClassName(...). Internally it parses the value into
    // course + semester instead of storing raw text.
    public void setClassName(String cn) {
        parseClassName(cn);
        syncClassName();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    // Parses strings like "BCA Sem 1", "BCA 1", "MCA Sem 3" into fields.
    private void parseClassName(String cn) {
        if (cn == null || cn.isBlank()) return;
        String[] parts = cn.trim().split("\\s+");
        this.course = parts[0];
        String last = parts[parts.length - 1];
        try { this.semester = Integer.parseInt(last); }
        catch (NumberFormatException ignored) {}
    }

    // Keeps the className string in sync whenever course or semester changes.
    private void syncClassName() {
        if (course != null && semester != null)
            this.className = course + " Sem " + semester;
    }

    // ── equals / hashCode ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student s = (Student) o;
        return Objects.equals(id, s.id) &&
                Objects.equals(rollNumber, s.rollNumber);
    }

    @Override
    public int hashCode() { return Objects.hash(id, rollNumber); }
}
