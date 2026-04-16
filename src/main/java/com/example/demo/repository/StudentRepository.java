package com.example.demo.repository;

import com.example.demo.Model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, String> {

    // FIX 1 — used by StudentController to detect duplicate rollNumbers
    // before attempting to save, so we can return a clear 409 error message
    // instead of letting MongoDB throw a cryptic DuplicateKeyException.
    boolean existsByRollNumber(String rollNumber);

    // Needed to exclude the current student when validating a rollNumber
    // change on PUT /students/{id} — a student should be allowed to keep
    // their own rollNumber, but not take one belonging to a different student.
    Optional<Student> findByRollNumber(String rollNumber);

    // Class-scoped queries used by TeacherClassService
    List<Student> findByCourseAndSemester(String course, int semester);
    List<Student> findByCourse(String course);
    List<Student> findBySemester(int semester);

    // Legacy className query kept for backward compat
    List<Student> findByClassName(String className);
}
