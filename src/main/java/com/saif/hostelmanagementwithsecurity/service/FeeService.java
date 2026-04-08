package com.saif.hostelmanagementwithsecurity.service;

import com.saif.hostelmanagementwithsecurity.dto.FeeDto;
import com.saif.hostelmanagementwithsecurity.entity.FeePayment;
import com.saif.hostelmanagementwithsecurity.entity.Student;
import com.saif.hostelmanagementwithsecurity.enums.PaymentStatus;
import com.saif.hostelmanagementwithsecurity.exception.BadRequestException;
import com.saif.hostelmanagementwithsecurity.exception.ConflictException;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.FeePaymentRepository;
import com.saif.hostelmanagementwithsecurity.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public FeeDto.Response generateFee(FeeDto.CreateRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));

        if (feePaymentRepository.existsByStudentIdAndFeeMonth(request.getStudentId(), request.getFeeMonth()))
            throw new ConflictException("Fee already generated for " + request.getFeeMonth());

        double total = request.getRoomRent()
                + request.getMealCharge()
                + request.getUtilityCharge()
                + request.getOtherCharge()
                + request.getFineAmount();

        FeePayment fee = FeePayment.builder()
                .invoiceNumber(generateInvoiceNumber())
                .student(student)
                .feeMonth(request.getFeeMonth())
                .totalAmount(total)
                .paidAmount(0.0)
                .dueAmount(total)
                .dueDate(request.getDueDate())
                .roomRent(request.getRoomRent())
                .mealCharge(request.getMealCharge())
                .utilityCharge(request.getUtilityCharge())
                .otherCharge(request.getOtherCharge())
                .fineAmount(request.getFineAmount())
                .status(PaymentStatus.PENDING)
                .build();

        return toResponse(feePaymentRepository.save(fee));
    }

    @Transactional
    public FeeDto.Response recordPayment(Long feeId, FeeDto.PayRequest request) {
        FeePayment fee = findById(feeId);

        if (fee.getStatus() == PaymentStatus.PAID)
            throw new BadRequestException("This fee is already fully paid");

        double newPaid = fee.getPaidAmount() + request.getAmount();
        if (newPaid > fee.getTotalAmount())
            throw new BadRequestException("Payment amount exceeds outstanding due amount");

        fee.setPaidAmount(newPaid);
        fee.setDueAmount(fee.getTotalAmount() - newPaid);
        fee.setPaymentMethod(request.getPaymentMethod());
        fee.setTransactionId(request.getTransactionId());
        fee.setRemarks(request.getRemarks());
        fee.setPaidDate(LocalDate.now());

        if (fee.getDueAmount() == 0.0) {
            fee.setStatus(PaymentStatus.PAID);
        } else {
            fee.setStatus(PaymentStatus.PARTIAL);
        }

        return toResponse(feePaymentRepository.save(fee));
    }

    @Transactional(readOnly = true)
    public List<FeeDto.Response> getFeesForStudent(Long studentId) {
        return feePaymentRepository.findByStudentId(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeDto.Response> getFeesByStatus(PaymentStatus status) {
        return feePaymentRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeDto.Response> getOverdueFees() {
        return feePaymentRepository.findOverdueFees(LocalDate.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeeDto.Summary getFeeSummary(String month) {
        FeeDto.Summary summary = new FeeDto.Summary();
        summary.setTotalCollected(feePaymentRepository.totalCollectedForMonth(month) != null
                ? feePaymentRepository.totalCollectedForMonth(month) : 0.0);
        summary.setTotalOutstanding(feePaymentRepository.totalOutstandingAmount() != null
                ? feePaymentRepository.totalOutstandingAmount() : 0.0);
        summary.setPendingCount(Long.valueOf(feePaymentRepository.findByStatus(PaymentStatus.PENDING).size()));
        summary.setOverdueCount(Long.valueOf(feePaymentRepository.findOverdueFees(LocalDate.now()).size()));
        summary.setPaidCount(Long.valueOf(feePaymentRepository.findByStatus(PaymentStatus.PAID).size()));
        return summary;
    }

    // Mark fees as OVERDUE automatically every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOverdueFees() {
        List<FeePayment> overdue = feePaymentRepository.findOverdueFees(LocalDate.now());
        overdue.forEach(f -> f.setStatus(PaymentStatus.OVERDUE));
        feePaymentRepository.saveAll(overdue);
    }

    // --- Helpers ---

    private FeePayment findById(Long id) {
        return feePaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeePayment", id));
    }

    private String generateInvoiceNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = feePaymentRepository.count() + 1;
        return String.format("INV-%s-%05d", year, count);
    }

    public FeeDto.Response toResponse(FeePayment f) {
        FeeDto.Response r = new FeeDto.Response();
        r.setId(f.getId());
        r.setInvoiceNumber(f.getInvoiceNumber());
        r.setStudentName(f.getStudent().getUser().getFullName());
        r.setStudentId(f.getStudent().getStudentId());
        r.setFeeMonth(f.getFeeMonth());
        r.setTotalAmount(f.getTotalAmount());
        r.setPaidAmount(f.getPaidAmount());
        r.setDueAmount(f.getDueAmount());
        r.setDueDate(f.getDueDate());
        r.setPaidDate(f.getPaidDate());
        r.setPaymentMethod(f.getPaymentMethod());
        r.setTransactionId(f.getTransactionId());
        r.setStatus(f.getStatus());
        r.setRoomRent(f.getRoomRent());
        r.setMealCharge(f.getMealCharge());
        r.setUtilityCharge(f.getUtilityCharge());
        r.setOtherCharge(f.getOtherCharge());
        r.setFineAmount(f.getFineAmount());
        r.setRemarks(f.getRemarks());
        r.setCreatedAt(f.getCreatedAt());
        return r;
    }
}
