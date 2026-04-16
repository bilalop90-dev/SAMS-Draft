package com.example.demo.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ReportService {
    void exportCSV(String studentId, String teacherId, String course,
                   Integer semester, HttpServletResponse response) throws Exception;
    void exportPDF(String studentId, String teacherId, String course,
                   Integer semester, HttpServletResponse response) throws Exception;
}
