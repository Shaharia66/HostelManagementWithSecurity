package com.saif.hostelmanagementwithsecurity.service;

import com.saif.hostelmanagementwithsecurity.dto.RoomDto;
import com.saif.hostelmanagementwithsecurity.entity.Bed;
import com.saif.hostelmanagementwithsecurity.entity.Room;
import com.saif.hostelmanagementwithsecurity.enums.BedStatus;
import com.saif.hostelmanagementwithsecurity.enums.RoomStatus;
import com.saif.hostelmanagementwithsecurity.exception.BadRequestException;
import com.saif.hostelmanagementwithsecurity.exception.ConflictException;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.BedRepository;
import com.saif.hostelmanagementwithsecurity.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    @Transactional
    public RoomDto.Response createRoom(RoomDto.CreateRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber()))
            throw new ConflictException("Room already exists: " + request.getRoomNumber());

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .block(request.getBlock())
                .floor(request.getFloor())
                .totalBeds(request.getTotalBeds())
                .occupiedBeds(0)
                .description(request.getDescription())
                .monthlyRent(request.getMonthlyRent())
                .status(RoomStatus.AVAILABLE)
                .hasAttachedBathroom(request.isHasAttachedBathroom())
                .hasAC(request.isHasAC())
                .hasWifi(request.isHasWifi())
                .build();

        room = roomRepository.save(room);

        // Auto-create beds for this room
        List<Bed> beds = new ArrayList<>();
        for (int i = 1; i <= request.getTotalBeds(); i++) {
            beds.add(Bed.builder()
                    .bedNumber("B" + i)
                    .status(BedStatus.AVAILABLE)
                    .room(room)
                    .build());
        }
        bedRepository.saveAll(beds);

        return toResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomDto.Response> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomDto.Response getRoomById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<RoomDto.Response> getRoomsByBlock(String block) {
        return roomRepository.findByBlock(block).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomDto.Response> getAvailableRooms() {
        return roomRepository.findRoomsWithAvailableBeds().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getAllBlocks() {
        return roomRepository.findAllBlocks();
    }

    @Transactional
    public RoomDto.Response updateRoom(Long id, RoomDto.UpdateRequest request) {
        Room room = findById(id);
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getMonthlyRent() != null) room.setMonthlyRent(request.getMonthlyRent());
        if (request.getStatus() != null)      room.setStatus(request.getStatus());
        room.setHasAttachedBathroom(request.isHasAttachedBathroom());
        room.setHasAC(request.isHasAC());
        room.setHasWifi(request.isHasWifi());
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomDto.BedInfo updateBedStatus(Long bedId, RoomDto.BedUpdateRequest request) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", bedId));
        if (bed.getStudent() != null && request.getStatus() != BedStatus.OCCUPIED)
            throw new BadRequestException("Cannot change status of an occupied bed. Unassign student first.");
        bed.setStatus(request.getStatus());
        bedRepository.save(bed);
        return toBedInfo(bed);
    }

    @Transactional(readOnly = true)
    public List<RoomDto.BedInfo> getBedsForRoom(Long roomId) {
        findById(roomId); // validate exists
        return bedRepository.findByRoomId(roomId).stream().map(this::toBedInfo).collect(Collectors.toList());
    }

    // --- Helpers ---

    private Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    public RoomDto.Response toResponse(Room room) {
        RoomDto.Response r = new RoomDto.Response();
        r.setId(room.getId());
        r.setRoomNumber(room.getRoomNumber());
        r.setBlock(room.getBlock());
        r.setFloor(room.getFloor());
        r.setTotalBeds(room.getTotalBeds());
        r.setOccupiedBeds(room.getOccupiedBeds());
        r.setAvailableBeds(room.getTotalBeds() - room.getOccupiedBeds());
        r.setDescription(room.getDescription());
        r.setMonthlyRent(room.getMonthlyRent());
        r.setStatus(room.getStatus());
        r.setHasAttachedBathroom(room.isHasAttachedBathroom());
        r.setHasAC(room.isHasAC());
        r.setHasWifi(room.isHasWifi());
        if (room.getBeds() != null)
            r.setBeds(room.getBeds().stream().map(this::toBedInfo).collect(Collectors.toList()));
        return r;
    }

    private RoomDto.BedInfo toBedInfo(Bed bed) {
        RoomDto.BedInfo info = new RoomDto.BedInfo();
        info.setBedId(bed.getId());
        info.setBedNumber(bed.getBedNumber());
        info.setStatus(bed.getStatus());
        if (bed.getRoom() != null) {
            info.setRoomNumber(bed.getRoom().getRoomNumber());
            info.setBlock(bed.getRoom().getBlock());
            info.setFloor(bed.getRoom().getFloor());
        }
        if (bed.getStudent() != null) {
            info.setOccupiedBy(bed.getStudent().getUser().getFullName());
            info.setStudentId(bed.getStudent().getStudentId());
        }
        return info;
    }
}
