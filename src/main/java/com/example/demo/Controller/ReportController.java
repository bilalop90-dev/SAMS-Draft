package com.example.demo.Controller;

import com.example.demo.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/report")
@Tag(name = "Reports", description = "Download student attendance reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Download CSV
    @Operation(summary = "Download CSV report",
            description = "Returns a downloadable CSV file with attendance summary and full record history for the student.")
    @GetMapping("/csv/{studentId}")
    public void downloadCSV(@PathVariable String studentId,
                            @RequestParam(required = false) String teacherId,
                            @RequestParam(required = false) String course,
                            @RequestParam(required = false) Integer semester,
                            HttpServletResponse response) throws Exception {
        reportService.exportCSV(studentId, teacherId, course, semester, response);
    }

    // Download PDF
    @Operation(summary = "Download PDF report",
            description = "Returns a downloadable PDF with risk color coding. Red for CRITICAL, yellow for WARNING, green for SAFE.")
    @GetMapping("/pdf/{studentId}")
    public void downloadPDF(@PathVariable String studentId,
                            @RequestParam(required = false) String teacherId,
                            @RequestParam(required = false) String course,
                            @RequestParam(required = false) Integer semester,
                            HttpServletResponse response) throws Exception {
        reportService.exportPDF(studentId, teacherId, course, semester, response);
    }
}
