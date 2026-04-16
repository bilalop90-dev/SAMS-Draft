package com.example.demo.service;

import com.example.demo.Model.*;
import com.example.demo.dto.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository repo;
    private final TeacherClassService teacherClassService;

    public DashboardServiceImpl(AttendanceRepository repo,
                                StudentRepository studentRepository,
                                TeacherClassService teacherClassService) {
        this.repo = repo;
        this.studentRepository = studentRepository;
        this.teacherClassService = teacherClassService;
    }

    // -------- EXISTING: Student Attention --------
    @Override
    public List<StudentAttention> getStudentsNeedingAttention(
            String teacherId, String course, Integer semester) {
        teacherClassService.validateTeacherClassAccess(teacherId, course, semester);
        List<Student> students = studentRepository.findByCourseAndSemester(course, semester);
        List<StudentAttention> result = new ArrayList<>();

        for (Student student : students) {
            List<Attendance> records = repo.findByStudentIdAndCourseAndSemester(
                    student.getRollNumber(), course, semester);
            int totalClasses = records.size();
            if (totalClasses == 0) continue;

            int attendedClasses = 0;
            for (Attendance a : records) {
                if ("Present".equalsIgnoreCase(a.getStatus())) attendedClasses++;
            }

            double percentage = (attendedClasses * 100.0) / totalClasses;

            if (percentage < 65) {
                String severity = (percentage < 50) ? "CRITICAL" : "WARNING";
                result.add(new StudentAttention(
                        student.getRollNumber(), student.getName(),
                        totalClasses, attendedClasses, percentage, severity));
            }
        }
        return result;
    }

    // -------- EXISTING: Recent Absentees --------
    @Override
    public List<RecentAbsentee> getRecentAbsentees(
            String teacherId, String course, Integer semester) {
        int CONSECUTIVE_LIMIT = 4;
        teacherClassService.validateTeacherClassAccess(teacherId, course, semester);
        List<Student> students = studentRepository.findByCourseAndSemester(course, semester);
        List<RecentAbsentee> result = new ArrayList<>();

        for (Student student : students) {
            List<Attendance> records =
                    repo.findByStudentIdAndCourseAndSemesterOrderByDateDesc(
                            student.getRollNumber(), course, semester);
            int consecutiveCount = 0;

            for (Attendance attendance : records) {
                if ("Absent".equalsIgnoreCase(attendance.getStatus())) {
                    consecutiveCount++;
                } else {
                    break;
                }
                if (consecutiveCount >= CONSECUTIVE_LIMIT) {
                    result.add(new RecentAbsentee(
                            student.getRollNumber(),
                            student.getName(),
                            consecutiveCount));
                    break;
                }
            }
        }
        return result;
    }

    // -------- NEW FEATURE 1: Trend Detection --------
    @Override
    public List<AttendanceTrend> getAttendanceTrends(
            String teacherId, String course, Integer semester) {
        teacherClassService.validateTeacherClassAccess(teacherId, course, semester);
        List<Student> students = studentRepository.findByCourseAndSemester(course, semester);
        List<AttendanceTrend> result = new ArrayList<>();

        LocalDate today = LocalDate.now();

        // Recent window  : last 14 days
        LocalDate recentStart   = today.minusDays(14);
        // Previous window: 15 to 28 days ago
        LocalDate previousStart = today.minusDays(28);
        LocalDate previousEnd   = today.minusDays(15);

        for (Student student : students) {
            List<Attendance> records =
                    repo.findByStudentIdAndCourseAndSemesterOrderByDateDesc(
                            student.getRollNumber(), course, semester);

            if (records.isEmpty()) continue;

            List<Attendance> recentRecords   = new ArrayList<>();
            List<Attendance> previousRecords = new ArrayList<>();

            for (Attendance a : records) {
                LocalDate date = LocalDate.parse(a.getDate());

                if (!date.isBefore(recentStart) && !date.isAfter(today)) {
                    recentRecords.add(a);
                } else if (!date.isBefore(previousStart) && !date.isAfter(previousEnd)) {
                    previousRecords.add(a);
                }
            }

            // Need data in both windows to compute a meaningful trend
            if (recentRecords.isEmpty() || previousRecords.isEmpty()) continue;

            double recentPercentage   = calculateWindowPercentage(recentRecords);
            double previousPercentage = calculateWindowPercentage(previousRecords);
            double change             = recentPercentage - previousPercentage;

            String trend;
            if (change >= 5)       trend = "IMPROVING";
            else if (change <= -5) trend = "DECLINING";
            else                   trend = "STABLE";

            result.add(new AttendanceTrend(
                    student.getRollNumber(),
                    student.getName(),
                    Math.round(recentPercentage   * 100.0) / 100.0,
                    Math.round(previousPercentage * 100.0) / 100.0,
                    Math.round(change             * 100.0) / 100.0,
                    trend
            ));
        }

        return result;
    }

//
//    public List<TopPerformer> getTopPerformers(int topN) {
//        return List.of();
//    }

    // -------- NEW FEATURE 2: Top Performers --------
    @Override
    public List<TopPerformer> getTopPerformers(
            String teacherId, String course, Integer semester, int topN) {
        teacherClassService.validateTeacherClassAccess(teacherId, course, semester);
        List<Student> students = studentRepository.findByCourseAndSemester(course, semester);
        List<TopPerformer> allPerformers = new ArrayList<>();

        for (Student student : students) {
            List<Attendance> records = repo.findByStudentIdAndCourseAndSemester(
                    student.getRollNumber(), course, semester);
            int totalClasses = records.size();
            if (totalClasses == 0) continue;

            int attendedClasses = 0;
            for (Attendance a : records) {
                if ("Present".equalsIgnoreCase(a.getStatus())) attendedClasses++;
            }

            double percentage = (attendedClasses * 100.0) / totalClasses;

            // Rank is set to 0 temporarily, will assign after sorting
            allPerformers.add(new TopPerformer(
                    0,
                    student.getRollNumber(),
                    student.getName(),
                    Math.round(percentage * 100.0) / 100.0,
                    totalClasses,
                    attendedClasses
            ));
        }

        // Sort descending by percentage
        allPerformers.sort((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()));

        // Assign ranks and limit to topN
        List<TopPerformer> topList = new ArrayList<>();
        int limit = Math.min(topN, allPerformers.size());

        for (int i = 0; i < limit; i++) {
            TopPerformer p = allPerformers.get(i);
            topList.add(new TopPerformer(
                    i + 1,
                    p.getStudentId(),
                    p.getStudentName(),
                    p.getPercentage(),
                    p.getTotalClasses(),
                    p.getAttendedClasses()
            ));
        }

        return topList;
    }

    // -------- HELPER --------
    private double calculateWindowPercentage(List<Attendance> records) {
        long present = records.stream()
                .filter(a -> "Present".equalsIgnoreCase(a.getStatus()))
                .count();
        return (present * 100.0) / records.size();
    }
}
