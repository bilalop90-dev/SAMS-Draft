package com.example.demo.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.demo.dto.*;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Teacher registration, student account creation, login, and password management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Teacher registers himself
    @Operation(summary = "Register a teacher",
            description = "Creates a new teacher account. Username must be unique.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> registerTeacher(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Teacher registered successfully",
                        authService.registerTeacher(request)));
    }

    // Teacher creates student account
    @Operation(summary = "Create student account",
            description = "Teacher creates a login account for a student. The studentId must match an existing rollNumber in the students collection.")
    @PostMapping("/create-student")
    public ResponseEntity<ApiResponse<LoginResponse>> createStudent(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester,
            @Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Student account created successfully",
                        authService.createStudent(teacherId, course, semester, request)));
    }

    // Login — both roles use same endpoint
    @Operation(summary = "Login",
            description = "Both teachers and students use this endpoint. Response contains role (TEACHER or STUDENT) and studentId (null for teachers). Frontend should use role to decide which dashboard to show.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Login successful",
                        authService.login(request)));
    }

    // Student changes own password
    @Operation(summary = "Change password",
            description = "Student changes their own password. Requires current password for verification.")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", null));
    }
}
