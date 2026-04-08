package com.saif.hostelmanagementwithsecurity.service;


import com.saif.hostelmanagementwithsecurity.dto.AnnouncementDto;
import com.saif.hostelmanagementwithsecurity.entity.Announcement;
import com.saif.hostelmanagementwithsecurity.entity.Teacher;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.AnnouncementRepository;
import com.saif.hostelmanagementwithsecurity.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public AnnouncementDto.Response create(AnnouncementDto.CreateRequest request, String username) {
        Teacher teacher = teacherRepository.findAll().stream()
                .filter(t -> t.getUser().getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found for user: " + username));

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .targetAudience(request.getTargetAudience())
                .expiresAt(request.getExpiresAt())
                .isUrgent(request.isUrgent())
                .isActive(true)
                .createdBy(teacher)
                .build();

        return toResponse(announcementRepository.save(announcement));
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto.Response> getActive() {
        return announcementRepository.findAllValidAnnouncements(LocalDateTime.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deactivate(Long id) {
        Announcement a = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));
        a.setActive(false);
        announcementRepository.save(a);
    }

    private AnnouncementDto.Response toResponse(Announcement a) {
        AnnouncementDto.Response r = new AnnouncementDto.Response();
        r.setId(a.getId());
        r.setTitle(a.getTitle());
        r.setContent(a.getContent());
        r.setTargetAudience(a.getTargetAudience());
        r.setExpiresAt(a.getExpiresAt());
        r.setUrgent(a.isUrgent());
        r.setActive(a.isActive());
        r.setCreatedBy(a.getCreatedBy().getUser().getFullName());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}