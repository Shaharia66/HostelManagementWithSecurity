package com.saif.hostelmanagementwithsecurity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Student extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String studentId;       // e.g. STU-2024-001

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String semester;

    private String session;         // e.g. 2023-2024

    private String guardianName;
    private String guardianPhone;
    private String address;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<FeePayment> feePayments;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<CheckInOut> checkInOuts;
}