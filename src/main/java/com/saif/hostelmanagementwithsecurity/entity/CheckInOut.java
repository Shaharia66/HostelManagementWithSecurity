package com.saif.hostelmanagementwithsecurity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "check_in_outs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CheckInOut extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private String reason;          // reason for going out

    private String destination;     // where going

    private boolean isNightOut = false;

    private String approvedBy;

    private boolean isReturned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}