package com.example.demo.dto;

import java.util.List;

/**
 * Response for GET /teachers/{teacherId}/my-students
 * Returns all students from all classes the teacher is assigned to,
 * grouped by class.
 */
public class MyStudentsResponse {

    private String teacherId;
    private String teacherName;
    private List<ClassStudents> classGroups;

    public MyStudentsResponse(String teacherId, String teacherName,
                              List<ClassStudents> classGroups) {
        this.teacherId   = teacherId;
        this.teacherName = teacherName;
        this.classGroups = classGroups;
    }

    public String              getTeacherId()   { return teacherId; }
    public String              getTeacherName() { return teacherName; }
    public List<ClassStudents> getClassGroups() { return classGroups; }

    // ── Nested DTO ────────────────────────────────────────────────────────
    public static class ClassStudents {
        private String            course;
        private int               semester;
        private List<StudentSummary> students;

        public ClassStudents(String course, int semester,
                             List<StudentSummary> students) {
            this.course    = course;
            this.semester  = semester;
            this.students  = students;
        }

        public String               getCourse()    { return course; }
        public int                  getSemester()  { return semester; }
        public List<StudentSummary> getStudents()  { return students; }
    }

    public static class StudentSummary {
        private String studentId;   // rollNumber
        private String name;
        private String course;
        private int    semester;

        public StudentSummary(String studentId, String name,
                              String course, int semester) {
            this.studentId = studentId;
            this.name      = name;
            this.course    = course;
            this.semester  = semester;
        }

        public String getStudentId() { return studentId; }
        public String getName()      { return name; }
        public String getCourse()    { return course; }
        public int    getSemester()  { return semester; }
    }
}