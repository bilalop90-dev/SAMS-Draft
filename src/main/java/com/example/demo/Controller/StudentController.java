package com.example.demo.Controller;

import com.example.demo.Model.Student;
import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.TeacherClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@Tag(name = "Students", description = "Manage student records")
public class StudentController {

    private final StudentRepository repo;
    private final TeacherClassService teacherClassService;

    public StudentController(StudentRepository repo,
                             TeacherClassService teacherClassService) {
        this.repo = repo;
        this.teacherClassService = teacherClassService;
    }

    @Operation(
            summary = "Add a student",
            description = "rollNumber must be unique and alphanumeric. " +
                    "course (e.g. BCA, MCA) and semester (1–8) are required. " +
                    "className is auto-derived and does not need to be sent."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Student>> addStudent(
            @RequestParam(required = false) String teacherId,
            @Valid @RequestBody Student student) {

        if (teacherId != null && !teacherId.isBlank()) {
            teacherClassService.validateTeacherClassAccess(
                    teacherId, student.getCourse(), student.getSemester());
        }

        // FIX 1 — reject duplicate rollNumber with a clear 409 before
        // MongoDB even sees the write. Without this check the driver throws
        // a DuplicateKeyException that bubbles up as a 500.
        if (repo.existsByRollNumber(student.getRollNumber())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(
                            "Roll number already exists: " + student.getRollNumber() +
                                    ". Each student must have a unique roll number."));
        }

        return ResponseEntity.ok(ApiResponse.success("Student added", repo.save(student)));
    }

    @Operation(summary = "Get all students")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents(
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer semester) {

        if (teacherId != null && !teacherId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(
                    teacherClassService.getStudentsForTeacherClass(teacherId, course, semester)));
        }

        if (course != null && !course.isBlank() && semester != null) {
            return ResponseEntity.ok(ApiResponse.success(
                    repo.findByCourseAndSemester(course, semester)));
        }

        return ResponseEntity.ok(ApiResponse.success(repo.findAll()));
    }

    @Operation(
            summary = "Update a student",
            description = "Pass MongoDB document id in the path, not rollNumber. " +
                    "If rollNumber is changed it must not conflict with another student."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(
            @PathVariable String id,
            @RequestParam(required = false) String teacherId,
            @Valid @RequestBody Student updatedStudent) {

        Student existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + id));

        if (teacherId != null && !teacherId.isBlank()) {
            teacherClassService.validateTeacherClassAccess(
                    teacherId, existing.getCourse(), existing.getSemester());
            teacherClassService.validateTeacherClassAccess(
                    teacherId, updatedStudent.getCourse(), updatedStudent.getSemester());
        }

        // FIX 1 (PUT) — if the rollNumber is being changed, make sure the
        // new value is not already taken by a DIFFERENT student.
        String newRoll = updatedStudent.getRollNumber();
        if (!newRoll.equals(existing.getRollNumber())) {
            if (repo.existsByRollNumber(newRoll)) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(
                                "Roll number already exists: " + newRoll +
                                        ". Choose a different roll number."));
            }
        }

        existing.setName(updatedStudent.getName());
        existing.setRollNumber(updatedStudent.getRollNumber());
        existing.setCourse(updatedStudent.getCourse());
        existing.setSemester(updatedStudent.getSemester());
        // className is re-derived automatically inside setCourse/setSemester

        return ResponseEntity.ok(ApiResponse.success("Student updated", repo.save(existing)));
    }

    @Operation(
            summary = "Delete a student",
            description = "Pass MongoDB document id in the path, not rollNumber."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable String id,
            @RequestParam(required = false) String teacherId) {
        Student existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + id));

        if (teacherId != null && !teacherId.isBlank()) {
            teacherClassService.validateTeacherClassAccess(
                    teacherId, existing.getCourse(), existing.getSemester());
        }

        repo.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted", null));
    }
}
