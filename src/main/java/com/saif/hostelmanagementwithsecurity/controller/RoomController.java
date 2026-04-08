package com.saif.hostelmanagementwithsecurity.controller;

import com.saif.hostelmanagementwithsecurity.dto.ApiResponse;
import com.saif.hostelmanagementwithsecurity.dto.RoomDto;
import com.saif.hostelmanagementwithsecurity.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room & Bed Management", description = "Create and manage rooms and beds")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Create a new room (beds are auto-generated)")
    public ResponseEntity<ApiResponse<RoomDto.Response>> createRoom(
            @Valid @RequestBody RoomDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully",
                        roomService.createRoom(request)));
    }

    @GetMapping
    @Operation(summary = "Get all rooms")
    public ResponseEntity<ApiResponse<List<RoomDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID with its beds")
    public ResponseEntity<ApiResponse<RoomDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomById(id)));
    }

    @GetMapping("/block/{block}")
    @Operation(summary = "Get all rooms in a block")
    public ResponseEntity<ApiResponse<List<RoomDto.Response>>> getByBlock(@PathVariable String block) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomsByBlock(block)));
    }

    @GetMapping("/available")
    @Operation(summary = "Get rooms with available beds")
    public ResponseEntity<ApiResponse<List<RoomDto.Response>>> getAvailable() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAvailableRooms()));
    }

    @GetMapping("/blocks")
    @Operation(summary = "Get list of all block names")
    public ResponseEntity<ApiResponse<List<String>>> getBlocks() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllBlocks()));
    }

    @GetMapping("/{roomId}/beds")
    @Operation(summary = "Get all beds for a room")
    public ResponseEntity<ApiResponse<List<RoomDto.BedInfo>>> getBeds(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getBedsForRoom(roomId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Update room details")
    public ResponseEntity<ApiResponse<RoomDto.Response>> update(
            @PathVariable Long id,
            @RequestBody RoomDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Room updated",
                roomService.updateRoom(id, request)));
    }

    @PutMapping("/beds/{bedId}")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    @Operation(summary = "Update bed status (AVAILABLE / MAINTENANCE)")
    public ResponseEntity<ApiResponse<RoomDto.BedInfo>> updateBed(
            @PathVariable Long bedId,
            @RequestBody RoomDto.BedUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Bed updated",
                roomService.updateBedStatus(bedId, request)));
    }
}