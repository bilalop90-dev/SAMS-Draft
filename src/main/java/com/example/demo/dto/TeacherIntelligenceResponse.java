package com.example.demo.dto;
import java.util.List;
public class TeacherIntelligenceResponse {

    private List<StudentPerformance> safeStudents;
    private List<StudentPerformance> warningStudents;
    private List<StudentPerformance> criticalStudents;

    public TeacherIntelligenceResponse(List<StudentPerformance> safeStudents, List<StudentPerformance> warningStudents, List<StudentPerformance> criticalStudents) {
        this.safeStudents = safeStudents;
        this.warningStudents = warningStudents;
        this.criticalStudents = criticalStudents;
    }

    public List<StudentPerformance> getSafeStudents() {
        return safeStudents;
    }

    public List<StudentPerformance> getWarningStudents() {
        return warningStudents;
    }

    public List<StudentPerformance> getCriticalStudents() {
        return criticalStudents;
    }
}
