package com.saif.hostelmanagementwithsecurity.controller;

import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.AttendanceDto;
import com.saif.hostelmanagementwithsecurity.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "Mark, update and report student attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Mark attendance for a single student")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> mark(
            @Valid @RequestBody AttendanceDto.MarkRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Attendance marked",
                attendanceService.markAttendance(request, userDetails.getUsername())));
    }

    @PostMapping("/mark/bulk")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Bulk mark attendance for multiple students")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> markBulk(
            @Valid @RequestBody AttendanceDto.BulkMarkRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Bulk attendance marked",
                attendanceService.markBulkAttendance(request, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Update an existing attendance record")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDto.MarkRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attendance updated",
                attendanceService.updateAttendance(id, request)));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Get all attendance records for a date")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByDate(date)));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get attendance history for a student within a date range")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getForStudent(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getStudentAttendance(studentId, from, to)));
    }

    @GetMapping("/student/{studentId}/monthly-report")
    @Operation(summary = "Get monthly attendance report for a student")
    public ResponseEntity<ApiResponse<AttendanceDto.MonthlyReport>> getMonthlyReport(
            @PathVariable Long studentId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getMonthlyReport(studentId, year, month)));
    }
}