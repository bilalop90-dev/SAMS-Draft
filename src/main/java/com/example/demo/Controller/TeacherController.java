package com.example.demo.Controller;

import com.example.demo.Model.TeacherClassMapping;
import com.example.demo.dto.*;
import com.example.demo.service.TeacherClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
@Tag(name = "Teacher Class Management",
        description = "Assign teachers to classes and get scoped student views")
public class TeacherController {

    private final TeacherClassService service;

    public TeacherController(TeacherClassService service) {
        this.service = service;
    }

    @Operation(
            summary = "Assign a class to a teacher",
            description = "Creates a mapping between a teacher (by their user id) and a course+semester. " +
                    "Idempotent — calling it twice has no effect."
    )
    @PostMapping("/{teacherId}/classes")
    public ResponseEntity<ApiResponse<TeacherClassMapping>> assignClass(
            @PathVariable String teacherId,
            @Valid @RequestBody AssignClassRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                "Class assigned to teacher",
                service.assignClass(teacherId, req)));
    }

    @Operation(
            summary = "Remove a class assignment from a teacher",
            description = "Removes the mapping for the given teacherId + course + semester."
    )
    @DeleteMapping("/{teacherId}/classes/{course}/{semester}")
    public ResponseEntity<ApiResponse<Void>> removeClass(
            @PathVariable String teacherId,
            @PathVariable String course,
            @PathVariable int semester) {
        service.removeClassAssignment(teacherId, course, semester);
        return ResponseEntity.ok(ApiResponse.success(
                "Class assignment removed", null));
    }

    @Operation(
            summary = "Get all classes assigned to a teacher",
            description = "Returns each course+semester the teacher is assigned to, with student count."
    )
    @GetMapping("/{teacherId}/classes")
    public ResponseEntity<ApiResponse<TeacherClassResponse>> getClasses(
            @PathVariable String teacherId) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getTeacherClasses(teacherId)));
    }

    @Operation(
            summary = "Get students scoped to a teacher",
            description = "Returns only students from the classes the teacher is assigned to, " +
                    "grouped by course and semester. This replaces GET /students for teacher dashboards."
    )
    @GetMapping("/{teacherId}/my-students")
    public ResponseEntity<ApiResponse<MyStudentsResponse>> getMyStudents(
            @PathVariable String teacherId) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getMyStudents(teacherId)));
    }
}