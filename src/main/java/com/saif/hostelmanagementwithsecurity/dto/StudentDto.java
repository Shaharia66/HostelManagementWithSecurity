package com.saif.hostelmanagementwithsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentDto {

    @Data
    public static class CreateRequest {
        @NotBlank private String fullName;
        @NotBlank private String email;
        @NotBlank private String username;
        @NotBlank private String password;
        private String phoneNumber;

        @NotBlank private String department;
        @NotBlank private String semester;
        private String session;
        private String guardianName;
        private String guardianPhone;
        private String address;
        private LocalDate checkInDate;
    }

    @Data
    public static class UpdateRequest {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String department;
        private String semester;
        private String session;
        private String guardianName;
        private String guardianPhone;
        private String address;
    }

    @Data
    public static class Response {
        private Long id;
        private String studentId;
        private String fullName;
        private String email;
        private String username;
        private String phoneNumber;
        private String department;
        private String semester;
        private String session;
        private String guardianName;
        private String guardianPhone;
        private String address;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private boolean active;
        private RoomDto.BedInfo bedInfo;
        private LocalDateTime createdAt;
    }

    @Data
    public static class AssignBedRequest {
        private Long bedId;
    }
}