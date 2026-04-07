package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findBySubmittedById(Long userId);
    List<Complaint> findByAssignedTeacherId(Long teacherId);
    List<Complaint> findByStatus(String status);
    List<Complaint> findByCategory(String category);
    long countByStatus(String status);
}