package com.example.demo.dto;

import java.util.List;

/**
 * Response for GET /teachers/{teacherId}/classes
 * Lists all course+semester pairs a teacher is assigned to,
 * plus the student count in each class.
 */
public class TeacherClassResponse {

    private String teacherId;
    private String teacherName;
    private List<ClassInfo> classes;

    public TeacherClassResponse(String teacherId, String teacherName,
                                List<ClassInfo> classes) {
        this.teacherId   = teacherId;
        this.teacherName = teacherName;
        this.classes     = classes;
    }

    public String        getTeacherId()   { return teacherId; }
    public String        getTeacherName() { return teacherName; }
    public List<ClassInfo> getClasses()  { return classes; }

    // ── Nested DTO ────────────────────────────────────────────────────────
    public static class ClassInfo {
        private String mappingId;
        private String course;
        private int    semester;
        private int    studentCount;

        public ClassInfo(String mappingId, String course,
                         int semester, int studentCount) {
            this.mappingId    = mappingId;
            this.course       = course;
            this.semester     = semester;
            this.studentCount = studentCount;
        }

        public String getMappingId()    { return mappingId; }
        public String getCourse()       { return course; }
        public int    getSemester()     { return semester; }
        public int    getStudentCount() { return studentCount; }
    }
}