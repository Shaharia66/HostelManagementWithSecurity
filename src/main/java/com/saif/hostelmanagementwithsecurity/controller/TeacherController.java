package com.saif.hostelmanagementwithsecurity.controller;

import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.TeacherDto;
import com.saif.hostelmanagementwithsecurity.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher Management", description = "Register and manage hostel teachers/wardens")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new teacher/warden")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> register(
            @Valid @RequestBody TeacherDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Teacher registered successfully",
                        teacherService.registerTeacher(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Get all active teachers")
    public ResponseEntity<ApiResponse<List<TeacherDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getAllTeachers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by ID")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getTeacherById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Update teacher details")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Teacher updated",
                teacherService.updateTeacher(id, request)));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a teacher account")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        teacherService.deactivateTeacher(id);
        return ResponseEntity.ok(ApiResponse.success("Teacher deactivated", null));
    }
}