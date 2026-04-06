package com.saif.hostelmanagementwithsecurity.entity;

import com.saif.hostelmanagementwithsecurity.enums.BedStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beds")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Bed extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bedNumber;       // e.g. B1, B2

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedStatus status = BedStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToOne(mappedBy = "bed", fetch = FetchType.LAZY)
    private Student student;
}