package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.FeePayment;
import com.saif.hostelmanagementwithsecurity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    Optional<FeePayment> findByInvoiceNumber(String invoiceNumber);
    List<FeePayment> findByStudentId(Long studentId);
    List<FeePayment> findByStudentIdAndStatus(Long studentId, PaymentStatus status);
    List<FeePayment> findByStatus(PaymentStatus status);
    List<FeePayment> findByFeeMonth(String feeMonth);

    @Query("SELECT f FROM FeePayment f WHERE f.dueDate < :today AND f.status IN ('PENDING', 'PARTIAL')")
    List<FeePayment> findOverdueFees(@Param("today") LocalDate today);

    @Query("SELECT SUM(f.paidAmount) FROM FeePayment f WHERE f.feeMonth = :month")
    Double totalCollectedForMonth(@Param("month") String month);

    @Query("SELECT SUM(f.dueAmount) FROM FeePayment f WHERE f.status IN ('PENDING', 'PARTIAL', 'OVERDUE')")
    Double totalOutstandingAmount();

    boolean existsByStudentIdAndFeeMonth(Long studentId, String feeMonth);
}