package com.saif.hostelmanagementwithsecurity.service;

import com.saif.hostelmanagementwithsecurity.dto.AuthDto;
import com.saif.hostelmanagementwithsecurity.entity.User;
import com.saif.hostelmanagementwithsecurity.enums.Role;
import com.saif.hostelmanagementwithsecurity.exception.BadRequestException;
import com.saif.hostelmanagementwithsecurity.exception.ConflictException;
import com.saif.hostelmanagementwithsecurity.repository.UserRepository;
import com.saif.hostelmanagementwithsecurity.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        AuthDto.TokenResponse response = new AuthDto.TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtils.getExpirationMs());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());
        return response;
    }

    @Transactional
    public User registerUser(AuthDto.RegisterRequest request, Role defaultRole) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new ConflictException("Username already taken: " + request.getUsername());
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ConflictException("Email already registered: " + request.getEmail());

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(defaultRole)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public AuthDto.TokenResponse refreshToken(AuthDto.RefreshTokenRequest request) {
        String username = jwtUtils.extractUsername(request.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtils.isTokenValid(request.getRefreshToken(), userDetails))
            throw new BadRequestException("Invalid or expired refresh token");

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        User user = userRepository.findByUsername(username).orElseThrow();

        AuthDto.TokenResponse response = new AuthDto.TokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(request.getRefreshToken());
        response.setExpiresIn(jwtUtils.getExpirationMs());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());
        return response;
    }

    @Transactional
    public void changePassword(String username, AuthDto.ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new BadRequestException("Current password is incorrect");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}