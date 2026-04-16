package com.example.demo.service;

import com.example.demo.Model.Student;
import com.example.demo.Model.TeacherClassMapping;
import com.example.demo.Model.User;
import com.example.demo.dto.AssignClassRequest;
import com.example.demo.dto.MyStudentsResponse;
import com.example.demo.dto.TeacherClassResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherClassMappingRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherClassService {

    private final TeacherClassMappingRepository mappingRepo;
    private final UserRepository               userRepo;
    private final StudentRepository            studentRepo;

    public TeacherClassService(TeacherClassMappingRepository mappingRepo,
                               UserRepository userRepo,
                               StudentRepository studentRepo) {
        this.mappingRepo = mappingRepo;
        this.userRepo    = userRepo;
        this.studentRepo = studentRepo;
    }

    // ── ASSIGN a teacher to a class ────────────────────────────────────────
    public TeacherClassMapping assignClass(String teacherId,
                                           AssignClassRequest req) {
        User teacher = userRepo.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found: " + teacherId));

        if (!"TEACHER".equals(teacher.getRole())) {
            throw new IllegalArgumentException(
                    "User is not a teacher: " + teacherId);
        }

        // Idempotent — if already assigned, return existing mapping
        return mappingRepo
                .findByTeacherIdAndCourseAndSemester(
                        teacherId, req.getCourse(), req.getSemester())
                .orElseGet(() -> mappingRepo.save(new TeacherClassMapping(
                        null,
                        teacherId,
                        teacher.getName(),
                        req.getCourse(),
                        req.getSemester())));
    }

    // ── REMOVE a class from a teacher ──────────────────────────────────────
    public void removeClassAssignment(String teacherId, String course,
                                      int semester) {
        mappingRepo.deleteByTeacherIdAndCourseAndSemester(
                teacherId, course, semester);
    }

    // ── GET all classes for a teacher (with student counts) ────────────────
    public TeacherClassResponse getTeacherClasses(String teacherId) {
        User teacher = userRepo.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found: " + teacherId));

        List<TeacherClassMapping> mappings =
                mappingRepo.findByTeacherId(teacherId);

        List<TeacherClassResponse.ClassInfo> classInfos = new ArrayList<>();
        for (TeacherClassMapping m : mappings) {
            int count = studentRepo
                    .findByCourseAndSemester(m.getCourse(), m.getSemester())
                    .size();
            classInfos.add(new TeacherClassResponse.ClassInfo(
                    m.getId(), m.getCourse(), m.getSemester(), count));
        }

        return new TeacherClassResponse(teacherId, teacher.getName(), classInfos);
    }

    // ── GET all students scoped to a teacher ──────────────────────────────
    public MyStudentsResponse getMyStudents(String teacherId) {
        User teacher = userRepo.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found: " + teacherId));

        List<TeacherClassMapping> mappings =
                mappingRepo.findByTeacherId(teacherId);

        if (mappings.isEmpty()) {
            return new MyStudentsResponse(teacherId, teacher.getName(),
                    List.of());
        }

        List<MyStudentsResponse.ClassStudents> groups = new ArrayList<>();

        for (TeacherClassMapping m : mappings) {
            List<Student> students =
                    studentRepo.findByCourseAndSemester(
                            m.getCourse(), m.getSemester());

            List<MyStudentsResponse.StudentSummary> summaries =
                    new ArrayList<>();

            for (Student s : students) {
                summaries.add(new MyStudentsResponse.StudentSummary(
                        s.getRollNumber(),
                        s.getName(),
                        s.getCourse()   != null ? s.getCourse()   : m.getCourse(),
                        s.getSemester() != null ? s.getSemester() : m.getSemester()));
            }

            groups.add(new MyStudentsResponse.ClassStudents(
                    m.getCourse(), m.getSemester(), summaries));
        }

        return new MyStudentsResponse(teacherId, teacher.getName(), groups);
    }

    // ── CHECK if a teacher is allowed to access a student ─────────────────
    public boolean teacherCanAccessStudent(String teacherId, String studentRollNumber) {
        Student student = studentRepo.findByRollNumber(studentRollNumber)
                .orElse(null);
        if (student == null) return false;
        if (student.getCourse() == null || student.getSemester() == null) return true; // legacy — allow

        return mappingRepo
                .findByTeacherIdAndCourseAndSemester(
                        teacherId,
                        student.getCourse(),
                        student.getSemester())
                .isPresent();
    }

    public void validateTeacherClassAccess(String teacherId, String course, Integer semester) {
        if (teacherId == null || teacherId.isBlank()) {
            throw new IllegalArgumentException("teacherId is required");
        }
        if (course == null || course.isBlank() || semester == null) {
            throw new IllegalArgumentException("course and semester are required");
        }
        if (mappingRepo.findByTeacherIdAndCourseAndSemester(teacherId, course, semester).isEmpty()) {
            throw new IllegalArgumentException(
                    "You are not assigned to " + course + " semester " + semester);
        }
    }

    public List<Student> getStudentsForTeacherClass(String teacherId, String course, Integer semester) {
        validateTeacherClassAccess(teacherId, course, semester);
        return studentRepo.findByCourseAndSemester(course, semester);
    }

    public Student getStudentForTeacherClass(String teacherId, String studentRollNumber,
                                             String course, Integer semester) {
        validateTeacherClassAccess(teacherId, course, semester);

        Student student = studentRepo.findByRollNumber(studentRollNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No student found with roll number: " + studentRollNumber));

        if (!course.equals(student.getCourse()) || !semester.equals(student.getSemester())) {
            throw new IllegalArgumentException(
                    "Student " + studentRollNumber + " does not belong to "
                            + course + " semester " + semester);
        }

        return student;
    }
}
