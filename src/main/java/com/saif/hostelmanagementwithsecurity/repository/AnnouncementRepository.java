package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Announcement> findByCreatedById(Long teacherId);
    List<Announcement> findByTargetAudienceInAndIsActiveTrue(List<String> audiences);

    @Query("SELECT a FROM Announcement a WHERE a.isActive = true AND (a.expiresAt IS NULL OR a.expiresAt > :now)")
    List<Announcement> findAllValidAnnouncements(LocalDateTime now);
}