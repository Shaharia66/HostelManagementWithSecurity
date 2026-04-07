package com.saif.hostelmanagementwithsecurity.repository;


import com.saif.hostelmanagementwithsecurity.entity.Attendance;
import com.saif.hostelmanagementwithsecurity.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByStudentIdAndAttendanceDate(Long studentId, LocalDate date);
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByAttendanceDate(LocalDate date);
    List<Attendance> findByStudentIdAndAttendanceDateBetween(Long studentId, LocalDate from, LocalDate to);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = :status AND a.attendanceDate BETWEEN :from AND :to")
    long countByStudentAndStatusBetween(@Param("studentId") Long studentId,
                                        @Param("status") AttendanceStatus status,
                                        @Param("from") LocalDate from,
                                        @Param("to") LocalDate to);

    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.status = 'ABSENT'")
    List<Attendance> findAbsentStudentsForDate(@Param("date") LocalDate date);

    boolean existsByStudentIdAndAttendanceDate(Long studentId, LocalDate date);
}