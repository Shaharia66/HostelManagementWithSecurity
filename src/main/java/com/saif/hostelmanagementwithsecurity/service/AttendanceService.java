package com.saif.hostelmanagementwithsecurity.service;

import com.saif.hostelmanagementwithsecurity.dto.AttendanceDto;
import com.saif.hostelmanagementwithsecurity.entity.Attendance;
import com.saif.hostelmanagementwithsecurity.entity.Student;
import com.saif.hostelmanagementwithsecurity.entity.User;
import com.saif.hostelmanagementwithsecurity.enums.AttendanceStatus;
import com.saif.hostelmanagementwithsecurity.exception.BadRequestException;
import com.saif.hostelmanagementwithsecurity.exception.ConflictException;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.AttendanceRepository;
import com.saif.hostelmanagementwithsecurity.repository.StudentRepository;
import com.saif.hostelmanagementwithsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AttendanceDto.Response markAttendance(AttendanceDto.MarkRequest request, String markedByUsername) {
        if (attendanceRepository.existsByStudentIdAndAttendanceDate(
                request.getStudentId(), request.getAttendanceDate()))
            throw new ConflictException("Attendance already marked for this student on " + request.getAttendanceDate());

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));

        User markedBy = userRepository.findByUsername(markedByUsername).orElse(null);

        Attendance attendance = Attendance.builder()
                .student(student)
                .attendanceDate(request.getAttendanceDate())
                .status(request.getStatus())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .remarks(request.getRemarks())
                .markedBy(markedBy)
                .build();

        return toResponse(attendanceRepository.save(attendance));
    }

    @Transactional
    public List<AttendanceDto.Response> markBulkAttendance(AttendanceDto.BulkMarkRequest request,
                                                           String markedByUsername) {
        return request.getRecords().stream()
                .map(r -> {
                    r.setAttendanceDate(request.getAttendanceDate());
                    return markAttendance(r, markedByUsername);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceDto.Response updateAttendance(Long id, AttendanceDto.MarkRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
        attendance.setStatus(request.getStatus());
        if (request.getCheckInTime() != null)  attendance.setCheckInTime(request.getCheckInTime());
        if (request.getCheckOutTime() != null) attendance.setCheckOutTime(request.getCheckOutTime());
        if (request.getRemarks() != null)      attendance.setRemarks(request.getRemarks());
        return toResponse(attendanceRepository.save(attendance));
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto.Response> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto.Response> getStudentAttendance(Long studentId, LocalDate from, LocalDate to) {
        return attendanceRepository.findByStudentIdAndAttendanceDateBetween(studentId, from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceDto.MonthlyReport getMonthlyReport(Long studentId, int year, int month) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = YearMonth.of(year, month).atEndOfMonth();

        long present = attendanceRepository.countByStudentAndStatusBetween(
                studentId, AttendanceStatus.PRESENT, from, to);
        long absent  = attendanceRepository.countByStudentAndStatusBetween(
                studentId, AttendanceStatus.ABSENT, from, to);
        long late    = attendanceRepository.countByStudentAndStatusBetween(
                studentId, AttendanceStatus.LATE, from, to);
        long leave   = attendanceRepository.countByStudentAndStatusBetween(
                studentId, AttendanceStatus.LEAVE, from, to);
        long total   = present + absent + late + leave;

        AttendanceDto.MonthlyReport report = new AttendanceDto.MonthlyReport();
        report.setStudentName(student.getUser().getFullName());
        report.setStudentId(student.getStudentId());
        report.setMonth(String.format("%04d-%02d", year, month));
        report.setPresentDays(present);
        report.setAbsentDays(absent);
        report.setLateDays(late);
        report.setLeaveDays(leave);
        report.setTotalDays(total);
        report.setAttendancePercentage(total > 0 ? (double) present / total * 100 : 0);
        return report;
    }

    // --- Helpers ---

    private AttendanceDto.Response toResponse(Attendance a) {
        AttendanceDto.Response r = new AttendanceDto.Response();
        r.setId(a.getId());
        r.setStudentName(a.getStudent().getUser().getFullName());
        r.setStudentId(a.getStudent().getStudentId());
        r.setAttendanceDate(a.getAttendanceDate());
        r.setStatus(a.getStatus());
        r.setCheckInTime(a.getCheckInTime());
        r.setCheckOutTime(a.getCheckOutTime());
        r.setRemarks(a.getRemarks());
        r.setLeaveApproved(a.isLeaveApproved());
        r.setMarkedBy(a.getMarkedBy() != null ? a.getMarkedBy().getFullName() : null);
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}