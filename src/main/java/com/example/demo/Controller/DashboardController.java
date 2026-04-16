// previous code
//package com.example.demo.Controller;
//
//import com.example.demo.dto.*;
//import com.example.demo.service.DashboardService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/dashboard")
//public class DashboardController {
//
//    private final DashboardService service;
//
//    public DashboardController(DashboardService service) {
//        this.service = service;
//    }
//
//    @GetMapping("/students-needing-attention")
//    public List<StudentAttention> getStudentsNeedingAttention() {
//        return service.getStudentsNeedingAttention();
//    }
//
//    @GetMapping("/recent-absentees")
//    public List<RecentAbsentee> getRecentAbsentees() {
//        return service.getRecentAbsentees();
//    }
//
//    // NEW: Trend Detection
//    @GetMapping("/trends")
//    public List<AttendanceTrend> getAttendanceTrends() {
//        return service.getAttendanceTrends();
//    }
//
//    // NEW: Top Performers — default top 5, or pass ?topN=3
//    @GetMapping("/top-performers")
//    public List<TopPerformer> getTopPerformers(
//            @RequestParam(defaultValue = "5") int topN) {
//        return service.getTopPerformers(topN);
//    }
//}

package com.example.demo.Controller;

import com.example.demo.dto.*;
import com.example.demo.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @Operation(summary = "Students needing attention",
            description = "Returns students below 65%. severity is CRITICAL if below 50%, WARNING otherwise.")
    @GetMapping("/students-needing-attention")
    public ResponseEntity<ApiResponse<List<StudentAttention>>> getStudentsNeedingAttention(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getStudentsNeedingAttention(teacherId, course, semester)));
    }

    @Operation(summary = "Recent absentees",
            description = "Returns students with 4 or more consecutive absences. consecutiveAbsences field shows the count.")
    @GetMapping("/recent-absentees")
    public ResponseEntity<ApiResponse<List<RecentAbsentee>>> getRecentAbsentees(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getRecentAbsentees(teacherId, course, semester)));
    }

    @Operation(summary = "Attendance trends",
            description = "Compares last 14 days vs previous 14 days per student. trend is IMPROVING (>=+5%), DECLINING (<=-5%), or STABLE.")
    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<List<AttendanceTrend>>> getAttendanceTrends(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getAttendanceTrends(teacherId, course, semester)));
    }

    @Operation(summary = "Top performers",
            description = "Returns top N students by attendance percentage. Pass topN as query param (default 5). Students are ranked starting from 1.")
    @GetMapping("/top-performers")
    public ResponseEntity<ApiResponse<List<TopPerformer>>> getTopPerformers(
            @RequestParam String teacherId,
            @RequestParam String course,
            @RequestParam Integer semester,
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getTopPerformers(teacherId, course, semester, topN)));
    }
}
