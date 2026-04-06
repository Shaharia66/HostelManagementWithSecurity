package com.saif.hostelmanagementwithsecurity.entity;

import com.saif.hostelmanagementwithsecurity.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rooms")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Room extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;      // e.g. A-101

    @Column(nullable = false)
    private String block;           // e.g. A, B, C

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private Integer totalBeds;

    @Column(nullable = false)
    private Integer occupiedBeds;

    private String description;

    private Double monthlyRent;     // rent per bed per month

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(nullable = false)
    private boolean hasAttachedBathroom = false;

    private boolean hasAC = false;
    private boolean hasWifi = true;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Bed> beds;
}