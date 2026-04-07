package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.CheckInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInOutRepository extends JpaRepository<CheckInOut, Long> {
    List<CheckInOut> findByStudentId(Long studentId);
    List<CheckInOut> findByIsReturnedFalse();
    Optional<CheckInOut> findFirstByStudentIdAndIsReturnedFalseOrderByCheckInTimeDesc(Long studentId);
    List<CheckInOut> findByCheckInTimeBetween(LocalDateTime from, LocalDateTime to);
    List<CheckInOut> findByIsNightOutTrue();
}
