package com.saif.hostelmanagementwithsecurity.controller;

import com.saif.hostelmanagementwithsecurity.dto.AnnouncementDto;
import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.service.AnnouncementService;
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
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Create and view hostel announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Create a new announcement")
    public ResponseEntity<ApiResponse<AnnouncementDto.Response>> create(
            @Valid @RequestBody AnnouncementDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Announcement created",
                        announcementService.create(request, userDetails.getUsername())));
    }

    @GetMapping
    @Operation(summary = "Get all active, non-expired announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementDto.Response>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getActive()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Deactivate (soft-delete) an announcement")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        announcementService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement removed", null));
    }
}