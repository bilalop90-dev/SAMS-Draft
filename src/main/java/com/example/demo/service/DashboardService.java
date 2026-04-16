package com.example.demo.service;

import com.example.demo.dto.*;
import java.util.List;

public interface DashboardService {

    List<StudentAttention> getStudentsNeedingAttention(String teacherId, String course, Integer semester);
    List<RecentAbsentee> getRecentAbsentees(String teacherId, String course, Integer semester);

    // Attendance Trend
    List<AttendanceTrend> getAttendanceTrends(String teacherId, String course, Integer semester);
//    Top-Performer
    List<TopPerformer> getTopPerformers(String teacherId, String course, Integer semester, int topN);

}
