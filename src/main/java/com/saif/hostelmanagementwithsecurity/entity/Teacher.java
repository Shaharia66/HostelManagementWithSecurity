package com.saif.hostelmanagementwithsecurity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "teachers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Teacher extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String teacherId;       // e.g. TCH-2024-001

    @Column(nullable = false)
    private String designation;     // e.g. Warden, Assistant Warden, Tutor

    @Column(nullable = false)
    private String department;

    private String specialization;
    private String officeRoom;
    private String officeHours;     // e.g. "Sun-Thu 9AM-5PM"

    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Teacher can be assigned as a floor/block in-charge
    @Column
    private String assignedBlock;

    // Announcements created by this teacher
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Announcement> announcements;

    // Complaints handled by this teacher
    @OneToMany(mappedBy = "assignedTeacher", cascade = CascadeType.ALL)
    private List<Complaint> complaints;
}
