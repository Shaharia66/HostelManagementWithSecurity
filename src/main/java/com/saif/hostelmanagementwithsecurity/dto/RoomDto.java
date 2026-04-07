package com.saif.hostelmanagementwithsecurity.dto;

import com.saif.hostelmanagementwithsecurity.enums.BedStatus;
import com.saif.hostelmanagementwithsecurity.enums.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

public class RoomDto {

    @Data
    public static class CreateRequest {
        @NotBlank private String roomNumber;
        @NotBlank private String block;
        @NotNull  private Integer floor;
        @NotNull @Min(1) private Integer totalBeds;
        private String description;
        private Double monthlyRent;
        private boolean hasAttachedBathroom;
        private boolean hasAC;
        private boolean hasWifi = true;
    }

    @Data
    public static class UpdateRequest {
        private String description;
        private Double monthlyRent;
        private RoomStatus status;
        private boolean hasAttachedBathroom;
        private boolean hasAC;
        private boolean hasWifi;
    }

    @Data
    public static class Response {
        private Long id;
        private String roomNumber;
        private String block;
        private Integer floor;
        private Integer totalBeds;
        private Integer occupiedBeds;
        private Integer availableBeds;
        private String description;
        private Double monthlyRent;
        private RoomStatus status;
        private boolean hasAttachedBathroom;
        private boolean hasAC;
        private boolean hasWifi;
        private List<BedInfo> beds;
    }

    @Data
    public static class BedInfo {
        private Long bedId;
        private String bedNumber;
        private String roomNumber;
        private String block;
        private Integer floor;
        private BedStatus status;
        private String occupiedBy;    // student name if occupied
        private String studentId;
    }

    @Data
    public static class BedUpdateRequest {
        private BedStatus status;
    }
}