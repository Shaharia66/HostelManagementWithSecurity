package com.saif.hostelmanagementwithsecurity.service;

import com.saif.hostelmanagementwithsecurity.dto.CheckInOutDto;
import com.saif.hostelmanagementwithsecurity.entity.CheckInOut;
import com.saif.hostelmanagementwithsecurity.entity.Student;
import com.saif.hostelmanagementwithsecurity.exception.BadRequestException;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.CheckInOutRepository;
import com.saif.hostelmanagementwithsecurity.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInOutService {

    private final CheckInOutRepository checkInOutRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public CheckInOutDto.Response recordCheckOut(CheckInOutDto.CheckOutRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));

        // Check if student already checked out without checking back in
        checkInOutRepository.findFirstByStudentIdAndIsReturnedFalseOrderByCheckInTimeDesc(request.getStudentId())
                .ifPresent(c -> { throw new BadRequestException("Student is already checked out since " + c.getCheckInTime()); });

        CheckInOut record = CheckInOut.builder()
                .student(student)
                .checkInTime(LocalDateTime.now())
                .reason(request.getReason())
                .destination(request.getDestination())
                .isNightOut(request.isNightOut())
                .approvedBy(request.getApprovedBy())
                .isReturned(false)
                .build();

        return toResponse(checkInOutRepository.save(record));
    }

    @Transactional
    public CheckInOutDto.Response recordReturn(Long studentId) {
        CheckInOut record = checkInOutRepository
                .findFirstByStudentIdAndIsReturnedFalseOrderByCheckInTimeDesc(studentId)
                .orElseThrow(() -> new BadRequestException("No active check-out record found for this student"));

        record.setCheckOutTime(LocalDateTime.now());
        record.setReturned(true);
        return toResponse(checkInOutRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<CheckInOutDto.Response> getStudentHistory(Long studentId) {
        return checkInOutRepository.findByStudentId(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CheckInOutDto.Response> getStudentsCurrentlyOut() {
        return checkInOutRepository.findByIsReturnedFalse()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CheckInOutDto.Response toResponse(CheckInOut c) {
        CheckInOutDto.Response r = new CheckInOutDto.Response();
        r.setId(c.getId());
        r.setStudentName(c.getStudent().getUser().getFullName());
        r.setStudentId(c.getStudent().getStudentId());
        r.setCheckInTime(c.getCheckInTime());
        r.setCheckOutTime(c.getCheckOutTime());
        r.setReason(c.getReason());
        r.setDestination(c.getDestination());
        r.setNightOut(c.isNightOut());
        r.setReturned(c.isReturned());
        r.setApprovedBy(c.getApprovedBy());
        return r;
    }
}
