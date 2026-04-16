package com.example.demo.service;

import com.example.demo.Model.Attendance;
import com.example.demo.Model.Student;
import com.example.demo.dto.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository repo;
    private final TeacherClassService teacherClassService;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository repo,
                             TeacherClassService teacherClassService,
                             UserRepository userRepository) {
        this.repo = repo;
        this.teacherClassService = teacherClassService;
        this.userRepository = userRepository;
    }

    public Attendance saveAttendance(String teacherId, Attendance attendance) {
        if (attendance.getCourse() == null || attendance.getCourse().isBlank()
                || attendance.getSemester() == null) {
            throw new IllegalArgumentException("course and semester are required for attendance");
        }

        Student student = teacherClassService.getStudentForTeacherClass(
                teacherId,
                attendance.getStudentId(),
                attendance.getCourse(),
                attendance.getSemester());

        attendance.setStudentName(student.getName());
        attendance.setTeacherName(userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found: " + teacherId))
                .getName());
        return repo.save(attendance);
    }

    public List<Attendance> getAllAttendance(String teacherId, String course, Integer semester) {
        teacherClassService.validateTeacherClassAccess(teacherId, course, semester);
        return repo.findByCourseAndSemester(course, semester);
    }

    public Attendance updateAttendance(String id, Attendance updated) {
        // Throws instead of returning null
        Attendance existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found with id: " + id));

        existing.setStudentId(updated.getStudentId());
        existing.setStudentName(updated.getStudentName());
        existing.setDate(updated.getDate());
        existing.setStatus(updated.getStatus());
        existing.setTeacherName(updated.getTeacherName());

        return repo.save(existing);
    }

    public void deleteAttendance(String id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Attendance record not found with id: " + id);
        }
        repo.deleteById(id);
    }

    public void deleteAttendance(String teacherId, String id) {
        Attendance existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found with id: " + id));

        teacherClassService.validateTeacherClassAccess(
                teacherId, existing.getCourse(), existing.getSemester());

        repo.deleteById(id);
    }

    public List<Attendance> getAttendanceByStudent(String studentId) {
        List<Attendance> records = repo.findByStudentId(studentId);
        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No attendance records found for student: " + studentId);
        }
        return records;
    }

    public List<Attendance> getAttendanceByStudent(
            String teacherId, String studentId, String course, Integer semester) {
        teacherClassService.getStudentForTeacherClass(teacherId, studentId, course, semester);
        List<Attendance> records = repo.findByStudentIdAndCourseAndSemester(studentId, course, semester);
        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No attendance records found for student: " + studentId
                            + " in " + course + " semester " + semester);
        }
        return records;
    }

    public long getTotalClasses(String studentId) {
        return repo.findByStudentId(studentId).size();
    }

    public long getPresentClasses(String studentId) {
        return repo.findByStudentId(studentId)
                .stream()
                .filter(a -> "Present".equalsIgnoreCase(a.getStatus()))
                .count();
    }

    public double calculatePercentage(String studentId) {
        return calculatePercentage(repo.findByStudentId(studentId), studentId);
    }

    public double calculatePercentage(
            String teacherId, String studentId, String course, Integer semester) {
        return calculatePercentage(
                getAttendanceByStudent(teacherId, studentId, course, semester), studentId);
    }

    public long classesNeededFor75(String studentId) {
        long total = getTotalClasses(studentId);
        long present = getPresentClasses(studentId);
        return Math.max(3 * total - 4 * present, 0);
    }

    public long classesNeededFor75(
            String teacherId, String studentId, String course, Integer semester) {
        List<Attendance> records = getAttendanceByStudent(teacherId, studentId, course, semester);
        long total = records.size();
        long present = countPresent(records);
        return Math.max(3 * total - 4 * present, 0);
    }

    public AttendanceRiskResponse getRisk(String studentId) {
        double percentage = calculatePercentage(studentId);
        String risk;
        if (percentage >= 75)      risk = "SAFE";
        else if (percentage >= 65) risk = "WARNING";
        else                       risk = "CRITICAL";
        return new AttendanceRiskResponse(percentage, risk);
    }

    public AttendanceRiskResponse getRisk(
            String teacherId, String studentId, String course, Integer semester) {
        double percentage = calculatePercentage(teacherId, studentId, course, semester);
        return toRiskResponse(percentage);
    }

    public AttendanceRequirementResponse getRequirement(String studentId) {
        double percentage = calculatePercentage(studentId);
        long needed = classesNeededFor75(studentId);
        return new AttendanceRequirementResponse(percentage, (int) needed);
    }

    public AttendanceRequirementResponse getRequirement(
            String teacherId, String studentId, String course, Integer semester) {
        double percentage = calculatePercentage(teacherId, studentId, course, semester);
        long needed = classesNeededFor75(teacherId, studentId, course, semester);
        return new AttendanceRequirementResponse(percentage, (int) needed);
    }

    public TeacherDashboardResponse getDashboard(String teacherId, String course, Integer semester) {
        teacherClassService.validateTeacherClassAccess(teacherId, course, semester);
        List<Attendance> all = repo.findByCourseAndSemester(course, semester);
        if (all.isEmpty()) throw new ResourceNotFoundException(
                "No attendance data available");

        Map<String, List<Attendance>> byStudent =
                all.stream().collect(Collectors.groupingBy(Attendance::getStudentId));

        int totalStudents = byStudent.size();
        int safe = 0, warning = 0, critical = 0;
        double totalPercentage = 0;

        for (List<Attendance> records : byStudent.values()) {
            long total = records.size();
            long present = records.stream()
                    .filter(a -> "Present".equals(a.getStatus())).count();
            double percentage = (present * 100.0) / total;
            totalPercentage += percentage;

            if (percentage >= 75)      safe++;
            else if (percentage >= 65) warning++;
            else                       critical++;
        }

        double avg = totalPercentage / totalStudents;
        return new TeacherDashboardResponse(totalStudents, avg, safe, warning, critical);
    }

    public TeacherIntelligenceResponse getTeacherIntelligence(
            String teacherId, String course, Integer semester) {
        teacherClassService.validateTeacherClassAccess(teacherId, course, semester);
        List<Attendance> all = repo.findByCourseAndSemester(course, semester);
        if (all.isEmpty()) throw new ResourceNotFoundException(
                "No attendance data available");

        Map<String, List<Attendance>> byStudent =
                all.stream().collect(Collectors.groupingBy(Attendance::getStudentId));

        List<StudentPerformance> safe     = new ArrayList<>();
        List<StudentPerformance> warning  = new ArrayList<>();
        List<StudentPerformance> critical = new ArrayList<>();

        for (Map.Entry<String, List<Attendance>> entry : byStudent.entrySet()) {
            List<Attendance> records = entry.getValue();
            long total   = records.size();
            long present = records.stream()
                    .filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
            double percentage = (present * 100.0) / total;
            String name = records.get(0).getStudentName();

            StudentPerformance perf =
                    new StudentPerformance(entry.getKey(), name, percentage);

            if (percentage >= 75)      safe.add(perf);
            else if (percentage >= 65) warning.add(perf);
            else                       critical.add(perf);
        }

        return new TeacherIntelligenceResponse(safe, warning, critical);
    }

    private double calculatePercentage(List<Attendance> records, String studentId) {
        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No attendance data found for student: " + studentId);
        }

        long present = countPresent(records);
        return (present * 100.0) / records.size();
    }

    private long countPresent(List<Attendance> records) {
        return records.stream()
                .filter(a -> "Present".equalsIgnoreCase(a.getStatus()))
                .count();
    }

    private AttendanceRiskResponse toRiskResponse(double percentage) {
        String risk;
        if (percentage >= 75)      risk = "SAFE";
        else if (percentage >= 65) risk = "WARNING";
        else                       risk = "CRITICAL";
        return new AttendanceRiskResponse(percentage, risk);
    }
}
