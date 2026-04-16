package com.example.demo.repository;

import com.example.demo.Model.TeacherClassMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherClassMappingRepository
        extends MongoRepository<TeacherClassMapping, String> {

    /** All classes assigned to a specific teacher */
    List<TeacherClassMapping> findByTeacherId(String teacherId);

    /** All teachers assigned to a specific class */
    List<TeacherClassMapping> findByCourseAndSemester(String course, int semester);

    /** Check if a specific assignment already exists */
    Optional<TeacherClassMapping> findByTeacherIdAndCourseAndSemester(
            String teacherId, String course, int semester);

    /** Remove a specific assignment */
    void deleteByTeacherIdAndCourseAndSemester(
            String teacherId, String course, int semester);
}