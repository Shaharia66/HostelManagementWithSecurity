package com.saif.hostelmanagementwithsecurity.dto;

import lombok.Data;

@Data
public class DashBoardDto {
    // Students
    private long totalStudents;
    private long activeStudents;

    // Rooms & Beds
    private long totalRooms;
    private long availableRooms;
    private long totalBeds;
    private long occupiedBeds;
    private long availableBeds;

    // Finance
    private double monthlyCollected;
    private double totalOutstanding;
    private long pendingFees;
    private long overdueFees;

    // Attendance
    private long presentToday;
    private long absentToday;

    // Complaints
    private long openComplaints;
    private long resolvedComplaints;

    // Students currently out
    private long studentsCurrentlyOut;
}
