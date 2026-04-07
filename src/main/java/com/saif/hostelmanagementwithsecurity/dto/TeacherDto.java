package com.saif.hostelmanagementwithsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class TeacherDto {

    @Data
    public static class CreateRequest {
        @NotBlank private String fullName;
        @NotBlank private String email;
        @NotBlank private String username;
        @NotBlank private String password;
        private String phoneNumber;

        @NotBlank private String designation;
        @NotBlank private String department;
        private String specialization;
        private String officeRoom;
        private String officeHours;
        private String assignedBlock;
    }

    @Data
    public static class UpdateRequest {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String designation;
        private String department;
        private String specialization;
        private String officeRoom;
        private String officeHours;
        private String assignedBlock;
    }

    @Data
    public static class Response {
        private Long id;
        private String teacherId;
        private String fullName;
        private String email;
        private String username;
        private String phoneNumber;
        private String designation;
        private String department;
        private String specialization;
        private String officeRoom;
        private String officeHours;
        private String assignedBlock;
        private boolean active;
        private LocalDateTime createdAt;
    }
}