package com.saif.hostelmanagementwithsecurity.controller;

import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.StudentDto;
import com.saif.hostelmanagementwithsecurity.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "Register, update and manage students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Register a new student")
    public ResponseEntity<ApiResponse<StudentDto.Response>> register(
            @Valid @RequestBody StudentDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student registered successfully",
                        studentService.registerStudent(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Get all active students")
    public ResponseEntity<ApiResponse<List<StudentDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by internal ID")
    public ResponseEntity<ApiResponse<StudentDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentById(id)));
    }

    @GetMapping("/sid/{studentId}")
    @Operation(summary = "Get student by student ID (e.g. STU-2024-0001)")
    public ResponseEntity<ApiResponse<StudentDto.Response>> getByStudentId(
            @PathVariable String studentId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentByStudentId(studentId)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Search students by name")
    public ResponseEntity<ApiResponse<List<StudentDto.Response>>> search(
            @RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(studentService.searchStudents(name)));
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Get students without an assigned bed")
    public ResponseEntity<ApiResponse<List<StudentDto.Response>>> getUnassigned() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getUnassignedStudents()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Update student details")
    public ResponseEntity<ApiResponse<StudentDto.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated",
                studentService.updateStudent(id, request)));
    }

    @PostMapping("/{id}/assign-bed/{bedId}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Assign a bed to a student")
    public ResponseEntity<ApiResponse<StudentDto.Response>> assignBed(
            @PathVariable Long id, @PathVariable Long bedId) {
        return ResponseEntity.ok(ApiResponse.success("Bed assigned successfully",
                studentService.assignBed(id, bedId)));
    }

    @PostMapping("/{id}/unassign-bed")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Unassign student's current bed")
    public ResponseEntity<ApiResponse<StudentDto.Response>> unassignBed(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Bed unassigned",
                studentService.unassignBed(id)));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Deactivate (check out) a student from the hostel")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        studentService.deactivateStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student checked out successfully", null));
    }
}