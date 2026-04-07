package com.saif.hostelmanagementwithsecurity.dto;

import com.saif.hostelmanagementwithsecurity.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeeDto {

    @Data
    public static class CreateRequest {
        @NotNull  private Long studentId;
        @NotBlank private String feeMonth;       // "2024-01"
        @NotNull @Positive private Double roomRent;
        private Double mealCharge = 0.0;
        private Double utilityCharge = 0.0;
        private Double otherCharge = 0.0;
        private Double fineAmount = 0.0;
        @NotNull  private LocalDate dueDate;
    }

    @Data
    public static class PayRequest {
        @NotNull @Positive private Double amount;
        @NotBlank private String paymentMethod;   // CASH, BANK_TRANSFER, BKASH, NAGAD
        private String transactionId;
        private String remarks;
    }

    @Data
    public static class Response {
        private Long id;
        private String invoiceNumber;
        private String studentName;
        private String studentId;
        private String feeMonth;
        private Double totalAmount;
        private Double paidAmount;
        private Double dueAmount;
        private LocalDate dueDate;
        private LocalDate paidDate;
        private String paymentMethod;
        private String transactionId;
        private PaymentStatus status;
        private Double roomRent;
        private Double mealCharge;
        private Double utilityCharge;
        private Double otherCharge;
        private Double fineAmount;
        private String remarks;
        private LocalDateTime createdAt;
    }

    @Data
    public static class Summary {
        private Double totalCollected;
        private Double totalOutstanding;
        private Long pendingCount;
        private Long overdueCount;
        private Long paidCount;
    }
}
