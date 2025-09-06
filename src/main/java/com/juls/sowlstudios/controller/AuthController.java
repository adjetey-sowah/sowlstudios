package com.juls.sowlstudios.controller;


import com.juls.sowlstudios.dto.request.LoginRequest;
import com.juls.sowlstudios.dto.response.ApiResponse;
import com.juls.sowlstudios.dto.response.LoginResponse;
import com.juls.sowlstudios.entity.Admin;
import com.juls.sowlstudios.service.AdminService;
import com.juls.sowlstudios.service.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateToken(authentication);

            Admin admin = adminService.findByUsername(loginRequest.getUsername());
            adminService.updateLastLogin(loginRequest.getUsername());

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(jwt)
                    .username(admin.getUsername())
                    .email(admin.getEmail())
                    .fullName(admin.getFullName())
                    .role(admin.getRole())
                    .build();

            log.info("Admin {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));

        } catch (Exception e) {
            log.error("Login failed for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Admin>> getProfile(Authentication authentication) {
        Admin admin = adminService.findByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(admin));
    }
}