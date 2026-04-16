package com.example.demo.repository;
import com.example.demo.Model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AttendanceRepository extends MongoRepository<Attendance, String>{
    List<Attendance> findByStudentId(String studentId);

    long countByStudentId(String studentId);
    long countByStudentIdAndStatus(String studentId, String status);

    List<Attendance> findAll();

    List<Attendance> findByStudentIdOrderByDateDesc(String studentId);

    // ── NEW: class-scoped queries ───────────────────────────────────────
    List<Attendance> findByCourseAndSemester(String course, int semester);
    List<Attendance> findByCourse(String course);
    List<Attendance> findByTeacherName(String teacherName);

    // ── NEW: student + class combined ───────────────────────────────────
    List<Attendance> findByStudentIdAndCourseAndSemester(
            String studentId, String course, int semester);

    List<Attendance> findByStudentIdAndCourseAndSemesterOrderByDateDesc(
            String studentId, String course, int semester);

}
