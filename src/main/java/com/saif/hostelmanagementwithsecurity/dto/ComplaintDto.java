package com.saif.hostelmanagementwithsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class ComplaintDto {

    @Data
    public static class CreateRequest {
        @NotBlank private String subject;
        @NotBlank private String description;
        private String category; // MAINTENANCE, FOOD, SECURITY, FACILITIES, OTHER
    }

    @Data
    public static class UpdateRequest {
        private String status;
        private String resolution;
        private Long assignedTeacherId;
    }

    @Data
    public static class Response {
        private Long id;
        private String subject;
        private String description;
        private String category;
        private String status;
        private String resolution;
        private String submittedBy;
        private String assignedTeacher;
        private LocalDateTime resolvedAt;
        private LocalDateTime createdAt;
    }
}