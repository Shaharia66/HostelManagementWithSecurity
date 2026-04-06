package com.saif.hostelmanagementwithsecurity.entity;

import com.saif.hostelmanagementwithsecurity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "fee_payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FeePayment extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;   // e.g. INV-2024-0001

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double paidAmount;

    private Double dueAmount;

    @Column(nullable = false)
    private String feeMonth;        // e.g. "2024-01"

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate paidDate;

    private String paymentMethod;   // CASH, BANK_TRANSFER, MOBILE_BANKING (bKash, Nagad)
    private String transactionId;

    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Fee breakdown
    private Double roomRent;
    private Double mealCharge;
    private Double utilityCharge;
    private Double otherCharge;
    private Double fineAmount;
}