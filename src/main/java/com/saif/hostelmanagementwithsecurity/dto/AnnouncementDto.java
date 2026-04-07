package com.saif.hostelmanagementwithsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class AnnouncementDto {

    @Data
    public static class CreateRequest {
        @NotBlank private String title;
        @NotBlank private String content;
        private String targetAudience = "ALL"; // ALL, STUDENTS, TEACHERS
        private LocalDateTime expiresAt;
        private boolean isUrgent = false;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String targetAudience;
        private LocalDateTime expiresAt;
        private boolean isUrgent;
        private boolean isActive;
        private String createdBy;
        private LocalDateTime createdAt;
    }
}