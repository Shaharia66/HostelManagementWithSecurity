package com.saif.hostelmanagementwithsecurity.service;

import com.saif.hostelmanagementwithsecurity.dto.ComplaintDto;
import com.saif.hostelmanagementwithsecurity.entity.Complaint;
import com.saif.hostelmanagementwithsecurity.entity.Teacher;
import com.saif.hostelmanagementwithsecurity.entity.User;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.ComplaintRepository;
import com.saif.hostelmanagementwithsecurity.repository.TeacherRepository;
import com.saif.hostelmanagementwithsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public ComplaintDto.Response submit(ComplaintDto.CreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Complaint complaint = Complaint.builder()
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory())
                .status("OPEN")
                .submittedBy(user)
                .build();

        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional
    public ComplaintDto.Response update(Long id, ComplaintDto.UpdateRequest request) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));

        if (request.getStatus() != null)     complaint.setStatus(request.getStatus());
        if (request.getResolution() != null) complaint.setResolution(request.getResolution());
        if ("RESOLVED".equals(request.getStatus())) complaint.setResolvedAt(LocalDateTime.now());

        if (request.getAssignedTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getAssignedTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.getAssignedTeacherId()));
            complaint.setAssignedTeacher(teacher);
        }

        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional(readOnly = true)
    public List<ComplaintDto.Response> getAll() {
        return complaintRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintDto.Response> getByStatus(String status) {
        return complaintRepository.findByStatus(status).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintDto.Response> getMyComplaints(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return complaintRepository.findBySubmittedById(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ComplaintDto.Response toResponse(Complaint c) {
        ComplaintDto.Response r = new ComplaintDto.Response();
        r.setId(c.getId());
        r.setSubject(c.getSubject());
        r.setDescription(c.getDescription());
        r.setCategory(c.getCategory());
        r.setStatus(c.getStatus());
        r.setResolution(c.getResolution());
        r.setSubmittedBy(c.getSubmittedBy().getFullName());
        r.setAssignedTeacher(c.getAssignedTeacher() != null
                ? c.getAssignedTeacher().getUser().getFullName() : null);
        r.setResolvedAt(c.getResolvedAt());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}