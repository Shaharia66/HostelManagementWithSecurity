package com.saif.hostelmanagementwithsecurity.dto;

import com.saif.hostelmanagementwithsecurity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;

        @NotBlank @Size(min = 6, max = 100)
        private String password;

        @NotBlank @Email
        private String email;

        @NotBlank
        private String fullName;

        private String phoneNumber;

        private Role role;
    }

    @Data
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private Long expiresIn;
        private String username;
        private String role;
        private Long userId;
    }

    @Data
    public static class RefreshTokenRequest {
        @NotBlank private String refreshToken;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank private String currentPassword;
        @NotBlank @Size(min = 6) private String newPassword;
    }
}