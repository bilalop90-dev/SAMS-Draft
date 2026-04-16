package com.example.demo.service;

import com.example.demo.Model.User;
import com.example.demo.Model.Student;
import com.example.demo.dto.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TeacherClassService teacherClassService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           TeacherClassService teacherClassService) {
        this.userRepository  = userRepository;
        this.teacherClassService = teacherClassService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // -------- Teacher registers himself --------
    @Override
    public LoginResponse registerTeacher(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already exists: " + request.getUsername());
        }

        User user = new User(null, request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                "TEACHER", request.getName(), null);

        userRepository.save(user); // Mongo assigns id after save

        // Now user.getId() is available
        return new LoginResponse(
                user.getUsername(),
                user.getName(),
                user.getRole(),
                null,           // studentId — null for teachers
                user.getId()    // teacherId — their own User.id
        );
    }

    // -------- Teacher creates student account --------
    @Override
    public LoginResponse createStudent(String teacherId, String course,
                                       Integer semester, CreateStudentRequest request) {

        // Check username not already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already exists: " + request.getUsername());
        }

        Student student = teacherClassService.getStudentForTeacherClass(
                teacherId, request.getStudentId(), course, semester);

        if (!student.getName().equalsIgnoreCase(request.getName().trim())) {
            throw new ResourceNotFoundException(
                    "Student name does not match the enrolled record for roll number: "
                            + request.getStudentId());
        }

        User user = new User(
                null,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // hash password
                "STUDENT",
                student.getName(),
                request.getStudentId() // link to students collection
        );

        userRepository.save(user);

        return new LoginResponse(
                user.getUsername(),
                user.getName(),
                user.getRole(),
                user.getStudentId()
        );
    }

    // -------- Login — both roles --------
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        return new LoginResponse(
                user.getUsername(),
                user.getName(),
                user.getRole(),
                user.getStudentId(),  // null for teachers
                user.getTeacherId()   // null for students, User.id for teachers
        );
    }

    // -------- Student changes own password --------
    @Override
    public void changePassword(ChangePasswordRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + request.getUsername()));

        // Verify current password first — exactly like we discussed
        // in our security conversation — sensitive action needs
        // current password confirmation
        if (!passwordEncoder.matches(
                request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }

        // Hash new password before saving — never store plain text
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
