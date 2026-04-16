package com.example.demo.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.demo.Model.Attendance;
import com.example.demo.dto.*;
import com.example.demo.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@Tag(name = "Attendance", description = "Mark attendance, view records, and get analytics per student")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @Operation(summary = "Mark attendance",
            description = "studentId must match an existing student rollNumber. date must be YYYY-MM-DD format. status must be exactly Present or Absent.")
    @PostMapping
    public ResponseEntity<ApiResponse<Attendance>> addAttendance(
            @RequestParam String teacherId,
            @Valid @RequestBody Attendance attendance) {  // @Valid triggers validation
        return ResponseEntity.ok(
                ApiResponse.success("Attendance recorded", service.saveAttendance(teacherId, attendance)));
    }
    @Operation(summary = "Get all attendance records")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Attendance>>> getAllAttendance(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getAllAttendance(teacherId, course, semester)));
    }
    @Operation(summary = "Update attendance record")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Attendance>> updateAttendance(
            @PathVariable String id,
            @Valid @RequestBody Attendance updated) {  // @Valid here too
        return ResponseEntity.ok(
                ApiResponse.success("Attendance updated", service.updateAttendance(id, updated)));
    }
    @Operation(summary = "Delete attendance record")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(
            @PathVariable String id,
            @RequestParam(required = false) String teacherId) {
        if (teacherId != null && !teacherId.isBlank()) {
            service.deleteAttendance(teacherId, id);
        } else {
            service.deleteAttendance(id);
        }
        return ResponseEntity.ok(ApiResponse.success("Attendance deleted", null));
    }
    @Operation(summary = "Get attendance records for a student",
            description = "Pass the student rollNumber as studentId. Returns all attendance records sorted by date.")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceByStudent(
            @PathVariable String studentId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer semester) {
        if (teacherId != null && !teacherId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(
                    service.getAttendanceByStudent(teacherId, studentId, course, semester)));
        }
        return ResponseEntity.ok(ApiResponse.success(service.getAttendanceByStudent(studentId)));
    }
    @Operation(summary = "Get attendance percentage",
            description = "Returns percentage, attended classes, and total classes for a student.")
    @GetMapping("/percentage/{studentId}")
    public ResponseEntity<ApiResponse<Double>> getAttendancePercentage(
            @PathVariable String studentId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer semester) {
        if (teacherId != null && !teacherId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(
                    service.calculatePercentage(teacherId, studentId, course, semester)));
        }
        return ResponseEntity.ok(ApiResponse.success(service.calculatePercentage(studentId)));
    }

    @Operation(summary = "Get risk status",
            description = "Returns SAFE (>=75%), WARNING (>=65%), or CRITICAL (<65%) for a student.")
    @GetMapping("/risk/{studentId}")
    public ResponseEntity<ApiResponse<AttendanceRiskResponse>> getAttendanceRisk(
            @PathVariable String studentId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer semester) {
        if (teacherId != null && !teacherId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(
                    service.getRisk(teacherId, studentId, course, semester)));
        }
        return ResponseEntity.ok(ApiResponse.success(service.getRisk(studentId)));
    }

    @Operation(summary = "Get classes needed for 75%",
            description = "Returns how many more consecutive classes the student needs to attend to reach 75%. Returns 0 if already at 75%.")
    @GetMapping("/required/{studentId}")
    public ResponseEntity<ApiResponse<AttendanceRequirementResponse>> getRequiredClasses(
            @PathVariable String studentId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer semester) {
        if (teacherId != null && !teacherId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(
                    service.getRequirement(teacherId, studentId, course, semester)));
        }
        return ResponseEntity.ok(ApiResponse.success(service.getRequirement(studentId)));
    }

    @Operation(summary = "Teacher dashboard overview",
            description = "Returns totalStudents, classAveragePercentage, safeCount, warningCount, criticalCount.")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<TeacherDashboardResponse>> getDashboard(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getDashboard(teacherId, course, semester)));
    }

    @Operation(summary = "Teacher intelligence view",
            description = "Returns all students grouped into safeStudents, warningStudents, criticalStudents lists.")
    @GetMapping("/intelligence")
    public ResponseEntity<ApiResponse<TeacherIntelligenceResponse>> getTeacherIntelligence(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getTeacherIntelligence(teacherId, course, semester)));
    }
}
