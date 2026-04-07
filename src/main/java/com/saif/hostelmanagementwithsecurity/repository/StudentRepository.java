package com.saif.hostelmanagementwithsecurity.repository;

import com.saif.hostelmanagementwithsecurity.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserId(Long userId);

    List<Student> findByActiveTrue();
    List<Student> findByDepartment(String department);
    List<Student> findBySemester(String semester);
    List<Student> findBySession(String session);
    List<Student> findByBedIsNull();  // unassigned students

    boolean existsByStudentId(String studentId);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.active = true")
    long countActiveStudents();

    @Query("SELECT s FROM Student s WHERE s.bed IS NOT NULL AND s.active = true")
    List<Student> findAllAssignedStudents();

    @Query("SELECT s FROM Student s WHERE s.user.fullName LIKE %:name% AND s.active = true")
    List<Student> searchByName(@Param("name") String name);
}
