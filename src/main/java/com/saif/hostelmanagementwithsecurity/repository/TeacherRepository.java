package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherId(String teacherId);
    Optional<Teacher> findByUserId(Long userId);
    List<Teacher> findByActiveTrue();
    List<Teacher> findByDepartment(String department);
    List<Teacher> findByAssignedBlock(String block);
    boolean existsByTeacherId(String teacherId);
}
