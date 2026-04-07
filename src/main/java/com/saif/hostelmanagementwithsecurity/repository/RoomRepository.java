package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.Room;
import com.saif.hostelmanagementwithsecurity.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByBlock(String block);
    List<Room> findByFloor(Integer floor);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByBlockAndStatus(String block, RoomStatus status);
    boolean existsByRoomNumber(String roomNumber);

    @Query("SELECT DISTINCT r.block FROM Room r ORDER BY r.block")
    List<String> findAllBlocks();

    @Query("SELECT r FROM Room r WHERE r.occupiedBeds < r.totalBeds AND r.status = 'AVAILABLE'")
    List<Room> findRoomsWithAvailableBeds();

    @Query("SELECT SUM(r.totalBeds) FROM Room r")
    Long countTotalBeds();

    @Query("SELECT SUM(r.occupiedBeds) FROM Room r")
    Long countOccupiedBeds();
}
