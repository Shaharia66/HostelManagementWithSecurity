package com.saif.hostelmanagementwithsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public class CheckInOutDto {

    @Data
    public static class CheckOutRequest {
        @NotNull  private Long studentId;
        @NotBlank private String reason;
        private String destination;
        private boolean isNightOut;
        private String approvedBy;
    }

    @Data
    public static class CheckInRequest {
        @NotNull private Long studentId;
    }

    @Data
    public static class Response {
        private Long id;
        private String studentName;
        private String studentId;
        private LocalDateTime checkInTime;
        private LocalDateTime checkOutTime;
        private String reason;
        private String destination;
        private boolean isNightOut;
        private boolean isReturned;
        private String approvedBy;
    }
}
