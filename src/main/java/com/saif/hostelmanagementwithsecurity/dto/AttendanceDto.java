package com.saif.hostelmanagementwithsecurity.dto;

import com.saif.hostelmanagementwithsecurity.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AttendanceDto {

    @Data
    public static class MarkRequest {
        @NotNull private Long studentId;
        @NotNull private LocalDate attendanceDate;
        @NotNull private AttendanceStatus status;
        private LocalTime checkInTime;
        private LocalTime checkOutTime;
        private String remarks;
    }

    @Data
    public static class BulkMarkRequest {
        @NotNull private LocalDate attendanceDate;
        @NotNull private List<MarkRequest> records;
    }

    @Data
    public static class Response {
        private Long id;
        private String studentName;
        private String studentId;
        private LocalDate attendanceDate;
        private AttendanceStatus status;
        private LocalTime checkInTime;
        private LocalTime checkOutTime;
        private String remarks;
        private boolean leaveApproved;
        private String markedBy;
        private LocalDateTime createdAt;
    }

    @Data
    public static class MonthlyReport {
        private String studentName;
        private String studentId;
        private String month;
        private long presentDays;
        private long absentDays;
        private long lateDays;
        private long leaveDays;
        private long totalDays;
        private double attendancePercentage;
    }
}