package com.saif.hostelmanagementwithsecurity.controller;


import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.CheckInOutDto;
import com.saif.hostelmanagementwithsecurity.service.CheckInOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkinout")
@RequiredArgsConstructor
@Tag(name = "Check-In / Check-Out", description = "Track student hostel exit and return")
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Record a student checking out of the hostel")
    public ResponseEntity<ApiResponse<CheckInOutDto.Response>> checkout(
            @Valid @RequestBody CheckInOutDto.CheckOutRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student checked out",
                checkInOutService.recordCheckOut(request)));
    }

    @PostMapping("/return/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Record a student returning to the hostel")
    public ResponseEntity<ApiResponse<CheckInOutDto.Response>> returnStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Student return recorded",
                checkInOutService.recordReturn(studentId)));
    }

    @GetMapping("/student/{studentId}/history")
    @Operation(summary = "Get check-in/out history for a student")
    public ResponseEntity<ApiResponse<List<CheckInOutDto.Response>>> getHistory(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(
                checkInOutService.getStudentHistory(studentId)));
    }

    @GetMapping("/out-now")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Get list of all students currently outside the hostel")
    public ResponseEntity<ApiResponse<List<CheckInOutDto.Response>>> getCurrentlyOut() {
        return ResponseEntity.ok(ApiResponse.success(
                checkInOutService.getStudentsCurrentlyOut()));
    }
}