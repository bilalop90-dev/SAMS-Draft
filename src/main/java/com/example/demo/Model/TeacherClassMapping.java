package com.example.demo.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Maps a teacher (by their User.id) to a specific course + semester.
 * One teacher can have many mappings (one per class they teach).
 * One class can have many teachers (e.g. different subjects).
 *
 * Collection: teacherClasses
 */
@Document(collection = "teacherClasses")
@CompoundIndexes({
        // Prevent the same teacher from being mapped to the same class twice
        @CompoundIndex(name = "teacher_class_unique",
                def = "{'teacherId': 1, 'course': 1, 'semester': 1}",
                unique = true)
})

public class TeacherClassMapping {
    @Id
    private String id;

    @NotBlank(message = "Teacher ID cannot be empty")
    private String teacherId;

    @NotBlank(message = "Teacher name cannot be empty")
    private String teacherName;

    @NotBlank(message = "Course cannot be empty")
    private String course;

    @Min(1)@Max(8)
    private int semester;

    public TeacherClassMapping() {}

    public TeacherClassMapping(String id, String teacherId, String teacherName, String course, int semester) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.course = course;
        this.semester = semester;
    }

    public String getId() {
        return id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getCourse() {
        return course;
    }

    public int getSemester() {
        return semester;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
}


