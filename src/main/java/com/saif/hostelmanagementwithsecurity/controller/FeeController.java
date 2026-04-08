package com.saif.hostelmanagementwithsecurity.controller;


import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.FeeDto;
import com.saif.hostelmanagementwithsecurity.enums.PaymentStatus;
import com.saif.hostelmanagementwithsecurity.service.FeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
@Tag(name = "Fee & Payment Management", description = "Generate fees, record payments, view reports")
public class FeeController {

    private final FeeService feeService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Generate a fee invoice for a student")
    public ResponseEntity<ApiResponse<FeeDto.Response>> generate(
            @Valid @RequestBody FeeDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fee invoice generated",
                        feeService.generateFee(request)));
    }

    @PostMapping("/{feeId}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Record a payment against a fee invoice")
    public ResponseEntity<ApiResponse<FeeDto.Response>> pay(
            @PathVariable Long feeId,
            @Valid @RequestBody FeeDto.PayRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment recorded",
                feeService.recordPayment(feeId, request)));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all fee records for a student")
    public ResponseEntity<ApiResponse<List<FeeDto.Response>>> getForStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(feeService.getFeesForStudent(studentId)));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Get fees by payment status")
    public ResponseEntity<ApiResponse<List<FeeDto.Response>>> getByStatus(
            @PathVariable PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success(feeService.getFeesByStatus(status)));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Get all overdue fee records")
    public ResponseEntity<ApiResponse<List<FeeDto.Response>>> getOverdue() {
        return ResponseEntity.ok(ApiResponse.success(feeService.getOverdueFees()));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Get financial summary for the current month")
    public ResponseEntity<ApiResponse<FeeDto.Summary>> getSummary(
            @RequestParam(required = false) String month) {
        if (month == null) {
            month = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return ResponseEntity.ok(ApiResponse.success(feeService.getFeeSummary(month)));
    }
}
