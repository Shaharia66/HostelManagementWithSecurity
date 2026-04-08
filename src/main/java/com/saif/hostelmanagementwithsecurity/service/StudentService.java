package com.saif.hostelmanagementwithsecurity.service;

import com.saif.hostelmanagementwithsecurity.dto.AuthDto;
import com.saif.hostelmanagementwithsecurity.dto.StudentDto;
import com.saif.hostelmanagementwithsecurity.entity.Bed;
import com.saif.hostelmanagementwithsecurity.entity.Student;
import com.saif.hostelmanagementwithsecurity.entity.User;
import com.saif.hostelmanagementwithsecurity.enums.BedStatus;
import com.saif.hostelmanagementwithsecurity.enums.Role;
import com.saif.hostelmanagementwithsecurity.enums.RoomStatus;
import com.saif.hostelmanagementwithsecurity.exception.BadRequestException;
import com.saif.hostelmanagementwithsecurity.exception.ConflictException;
import com.saif.hostelmanagementwithsecurity.exception.ResourceNotFoundException;
import com.saif.hostelmanagementwithsecurity.repository.BedRepository;
import com.saif.hostelmanagementwithsecurity.repository.RoomRepository;
import com.saif.hostelmanagementwithsecurity.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final AuthService authService;

    @Transactional
    public StudentDto.Response registerStudent(StudentDto.CreateRequest request) {
        // Create user account
        AuthDto.RegisterRequest userReq = new AuthDto.RegisterRequest();
        userReq.setUsername(request.getUsername());
        userReq.setPassword(request.getPassword());
        userReq.setEmail(request.getEmail());
        userReq.setFullName(request.getFullName());
        userReq.setPhoneNumber(request.getPhoneNumber());

        User user = authService.registerUser(userReq, Role.STUDENT);

        // Auto-generate student ID
        String studentId = generateStudentId();

        Student student = Student.builder()
                .studentId(studentId)
                .department(request.getDepartment())
                .semester(request.getSemester())
                .session(request.getSession())
                .guardianName(request.getGuardianName())
                .guardianPhone(request.getGuardianPhone())
                .address(request.getAddress())
                .checkInDate(request.getCheckInDate() != null ? request.getCheckInDate() : LocalDate.now())
                .active(true)
                .user(user)
                .build();

        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public List<StudentDto.Response> getAllStudents() {
        return studentRepository.findByActiveTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentDto.Response getStudentById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public StudentDto.Response getStudentByStudentId(String studentId) {
        return toResponse(studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId)));
    }

    @Transactional
    public StudentDto.Response updateStudent(Long id, StudentDto.UpdateRequest request) {
        Student student = findById(id);
        User user = student.getUser();

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null)    user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDepartment() != null)  student.setDepartment(request.getDepartment());
        if (request.getSemester() != null)    student.setSemester(request.getSemester());
        if (request.getSession() != null)     student.setSession(request.getSession());
        if (request.getGuardianName() != null) student.setGuardianName(request.getGuardianName());
        if (request.getGuardianPhone() != null) student.setGuardianPhone(request.getGuardianPhone());
        if (request.getAddress() != null)     student.setAddress(request.getAddress());

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentDto.Response assignBed(Long studentId, Long bedId) {
        Student student = findById(studentId);
        if (student.getBed() != null)
            throw new ConflictException("Student already has an assigned bed. Unassign first.");

        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", bedId));

        if (bed.getStatus() != BedStatus.AVAILABLE)
            throw new BadRequestException("Bed is not available. Current status: " + bed.getStatus());

        bed.setStatus(BedStatus.OCCUPIED);
        bed.setStudent(student);
        student.setBed(bed);

        // Update room occupancy
        var room = bed.getRoom();
        room.setOccupiedBeds(room.getOccupiedBeds() + 1);
        if (room.getOccupiedBeds() >= room.getTotalBeds()) {
            room.setStatus(RoomStatus.FULL);
        }
        roomRepository.save(room);
        bedRepository.save(bed);

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentDto.Response unassignBed(Long studentId) {
        Student student = findById(studentId);
        if (student.getBed() == null)
            throw new BadRequestException("Student does not have an assigned bed");

        Bed bed = student.getBed();
        bed.setStatus(BedStatus.AVAILABLE);
        bed.setStudent(null);
        student.setBed(null);

        var room = bed.getRoom();
        room.setOccupiedBeds(Math.max(0, room.getOccupiedBeds() - 1));
        if (room.getStatus() == RoomStatus.FULL)
            room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        bedRepository.save(bed);

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void deactivateStudent(Long id) {
        Student student = findById(id);
        if (student.getBed() != null) unassignBed(id);
        student.setActive(false);
        student.setCheckOutDate(LocalDate.now());
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDto.Response> searchStudents(String name) {
        return studentRepository.searchByName(name)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDto.Response> getUnassignedStudents() {
        return studentRepository.findByBedIsNull()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // --- Helpers ---

    private Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    private String generateStudentId() {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = studentRepository.count() + 1;
        return String.format("STU-%s-%04d", year, count);
    }

    public StudentDto.Response toResponse(Student s) {
        StudentDto.Response r = new StudentDto.Response();
        r.setId(s.getId());
        r.setStudentId(s.getStudentId());
        r.setFullName(s.getUser().getFullName());
        r.setEmail(s.getUser().getEmail());
        r.setUsername(s.getUser().getUsername());
        r.setPhoneNumber(s.getUser().getPhoneNumber());
        r.setDepartment(s.getDepartment());
        r.setSemester(s.getSemester());
        r.setSession(s.getSession());
        r.setGuardianName(s.getGuardianName());
        r.setGuardianPhone(s.getGuardianPhone());
        r.setAddress(s.getAddress());
        r.setCheckInDate(s.getCheckInDate());
        r.setCheckOutDate(s.getCheckOutDate());
        r.setActive(s.isActive());
        r.setCreatedAt(s.getCreatedAt());

        if (s.getBed() != null) {
            var bed = s.getBed();
            var room = bed.getRoom();
            var info = new com.saif.hostelmanagementwithsecurity.dto.RoomDto.BedInfo();
            info.setBedId(bed.getId());
            info.setBedNumber(bed.getBedNumber());
            info.setRoomNumber(room.getRoomNumber());
            info.setBlock(room.getBlock());
            info.setFloor(room.getFloor());
            info.setStatus(bed.getStatus());
            r.setBedInfo(info);
        }
        return r;
    }
}