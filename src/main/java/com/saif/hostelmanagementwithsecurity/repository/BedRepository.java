package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.Bed;
import com.saif.hostelmanagementwithsecurity.enums.BedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByRoomId(Long roomId);
    List<Bed> findByRoomIdAndStatus(Long roomId, BedStatus status);
    List<Bed> findByStatus(BedStatus status);
    Optional<Bed> findByRoomIdAndBedNumber(Long roomId, String bedNumber);
    boolean existsByRoomIdAndBedNumber(Long roomId, String bedNumber);
}
