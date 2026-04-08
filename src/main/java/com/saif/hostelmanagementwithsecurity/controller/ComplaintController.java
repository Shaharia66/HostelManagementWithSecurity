package com.saif.hostelmanagementwithsecurity.controller;

import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.ComplaintDto;
import com.saif.hostelmanagementwithsecurity.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaint Management", description = "Submit and manage student complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @Operation(summary = "Submit a new complaint (any authenticated user)")
    public ResponseEntity<ApiResponse<ComplaintDto.Response>> submit(
            @Valid @RequestBody ComplaintDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint submitted",
                        complaintService.submit(request, userDetails.getUsername())));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Get all complaints (admin/warden/teacher only)")
    public ResponseEntity<ApiResponse<List<ComplaintDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(complaintService.getAll()));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Get complaints by status: OPEN / IN_PROGRESS / RESOLVED / CLOSED")
    public ResponseEntity<ApiResponse<List<ComplaintDto.Response>>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(ApiResponse.success(complaintService.getByStatus(status)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get complaints submitted by the current user")
    public ResponseEntity<ApiResponse<List<ComplaintDto.Response>>> getMine(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                complaintService.getMyComplaints(userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Update complaint status / assign to teacher / add resolution")
    public ResponseEntity<ApiResponse<ComplaintDto.Response>> update(
            @PathVariable Long id,
            @RequestBody ComplaintDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Complaint updated",
                complaintService.update(id, request)));
    }
}