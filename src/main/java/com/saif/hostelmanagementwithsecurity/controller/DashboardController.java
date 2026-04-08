package com.saif.hostelmanagementwithsecurity.controller;
import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.DashBoardDto;
import com.saif.hostelmanagementwithsecurity.enums.AttendanceStatus;
import com.saif.hostelmanagementwithsecurity.enums.PaymentStatus;
import com.saif.hostelmanagementwithsecurity.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Summary statistics for the admin dashboard")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ComplaintRepository complaintRepository;
    private final CheckInOutRepository checkInOutRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN','TEACHER')")
    @Operation(summary = "Get full dashboard summary")
    public ResponseEntity<ApiResponse<DashBoardDto>> getDashboard() {
        DashBoardDto dto = new DashBoardDto();
        LocalDate today = LocalDate.now();
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // Students
        dto.setTotalStudents(studentRepository.count());
        dto.setActiveStudents(studentRepository.countActiveStudents());

        // Rooms & Beds
        dto.setTotalRooms(roomRepository.count());
        dto.setAvailableRooms(roomRepository.findRoomsWithAvailableBeds().size());
        Long totalBeds    = roomRepository.countTotalBeds();
        Long occupiedBeds = roomRepository.countOccupiedBeds();
        dto.setTotalBeds(totalBeds != null ? totalBeds : 0);
        dto.setOccupiedBeds(occupiedBeds != null ? occupiedBeds : 0);
        dto.setAvailableBeds(dto.getTotalBeds() - dto.getOccupiedBeds());

        // Finance
        Double collected = feePaymentRepository.totalCollectedForMonth(currentMonth);
        Double outstanding = feePaymentRepository.totalOutstandingAmount();
        dto.setMonthlyCollected(collected != null ? collected : 0.0);
        dto.setTotalOutstanding(outstanding != null ? outstanding : 0.0);
        dto.setPendingFees(feePaymentRepository.findByStatus(PaymentStatus.PENDING).size());
        dto.setOverdueFees(feePaymentRepository.findOverdueFees(today).size());

        // Attendance today
        long presentToday = attendanceRepository.findByAttendanceDate(today).stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absentToday = attendanceRepository.findByAttendanceDate(today).stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        dto.setPresentToday(presentToday);
        dto.setAbsentToday(absentToday);

        // Complaints
        dto.setOpenComplaints(complaintRepository.countByStatus("OPEN"));
        dto.setResolvedComplaints(complaintRepository.countByStatus("RESOLVED"));

        // Students outside right now
        dto.setStudentsCurrentlyOut(checkInOutRepository.findByIsReturnedFalse().size());

        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}