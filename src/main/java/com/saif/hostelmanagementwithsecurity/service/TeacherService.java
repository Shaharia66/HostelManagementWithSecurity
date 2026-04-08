package com.saif.hostelmanagementwithsecurity.service;


import com.saif.hostelmanagementwithsecurity.dto.AuthDto;
import com.saif.hostelmanagementwithsecurity.dto.TeacherDto;
import com.saif.hostelmanagementwithsecurity.entity.Teacher;
import com.saif.hostelmanagementwithsecurity.entity.User;
import com.saif.hostelmanagementwithsecurity.enums.Role;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final AuthService authService;

    @Transactional
    public TeacherDto.Response registerTeacher(TeacherDto.CreateRequest request) {
        AuthDto.RegisterRequest userReq = new AuthDto.RegisterRequest();
        userReq.setUsername(request.getUsername());
        userReq.setPassword(request.getPassword());
        userReq.setEmail(request.getEmail());
        userReq.setFullName(request.getFullName());
        userReq.setPhoneNumber(request.getPhoneNumber());

        User user = authService.registerUser(userReq, Role.TEACHER);

        String teacherId = generateTeacherId();

        Teacher teacher = Teacher.builder()
                .teacherId(teacherId)
                .designation(request.getDesignation())
                .department(request.getDepartment())
                .specialization(request.getSpecialization())
                .officeRoom(request.getOfficeRoom())
                .officeHours(request.getOfficeHours())
                .assignedBlock(request.getAssignedBlock())
                .active(true)
                .user(user)
                .build();

        return toResponse(teacherRepository.save(teacher));
    }

    @Transactional(readOnly = true)
    public List<TeacherDto.Response> getAllTeachers() {
        return teacherRepository.findByActiveTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherDto.Response getTeacherById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public TeacherDto.Response updateTeacher(Long id, TeacherDto.UpdateRequest request) {
        Teacher teacher = findById(id);
        User user = teacher.getUser();

        if (request.getFullName() != null)      user.setFullName(request.getFullName());
        if (request.getEmail() != null)         user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null)   user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDesignation() != null)   teacher.setDesignation(request.getDesignation());
        if (request.getDepartment() != null)    teacher.setDepartment(request.getDepartment());
        if (request.getSpecialization() != null) teacher.setSpecialization(request.getSpecialization());
        if (request.getOfficeRoom() != null)    teacher.setOfficeRoom(request.getOfficeRoom());
        if (request.getOfficeHours() != null)   teacher.setOfficeHours(request.getOfficeHours());
        if (request.getAssignedBlock() != null) teacher.setAssignedBlock(request.getAssignedBlock());

        return toResponse(teacherRepository.save(teacher));
    }

    @Transactional
    public void deactivateTeacher(Long id) {
        Teacher teacher = findById(id);
        teacher.setActive(false);
        teacherRepository.save(teacher);
    }

    // --- Helpers ---

    public Teacher findById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
    }

    private String generateTeacherId() {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = teacherRepository.count() + 1;
        return String.format("TCH-%s-%04d", year, count);
    }

    public TeacherDto.Response toResponse(Teacher t) {
        TeacherDto.Response r = new TeacherDto.Response();
        r.setId(t.getId());
        r.setTeacherId(t.getTeacherId());
        r.setFullName(t.getUser().getFullName());
        r.setEmail(t.getUser().getEmail());
        r.setUsername(t.getUser().getUsername());
        r.setPhoneNumber(t.getUser().getPhoneNumber());
        r.setDesignation(t.getDesignation());
        r.setDepartment(t.getDepartment());
        r.setSpecialization(t.getSpecialization());
        r.setOfficeRoom(t.getOfficeRoom());
        r.setOfficeHours(t.getOfficeHours());
        r.setAssignedBlock(t.getAssignedBlock());
        r.setActive(t.isActive());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}