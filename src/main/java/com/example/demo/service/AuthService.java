package com.example.demo.service;

import com.example.demo.dto.*;

public interface AuthService {
    LoginResponse registerTeacher(RegisterRequest request);
    LoginResponse createStudent(String teacherId, String course,
                                Integer semester, CreateStudentRequest request);
    LoginResponse login(LoginRequest request);
    void changePassword(ChangePasswordRequest request);
}
