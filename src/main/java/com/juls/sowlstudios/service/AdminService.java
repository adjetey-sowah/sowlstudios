package com.juls.sowlstudios.service;

import com.juls.sowlstudios.entity.Admin;
import com.juls.sowlstudios.repository.AdminRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default.username}")
    private String defaultUsername;

    @Value("${app.admin.default.password}")
    private String defaultPassword;

    @Value("${app.admin.default.email}")
    private String defaultEmail;

    @Value("${app.admin.default.fullName}")
    private String defaultFullName;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));

        if (!admin.getIsActive()) {
            throw new UsernameNotFoundException("Admin account is inactive: " + username);
        }

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(admin.getRole())
                .build();
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));
    }

    @Transactional
    public void updateLastLogin(String username) {
        adminRepository.updateLastLogin(username, LocalDateTime.now());
        log.info("Updated last login for admin: {}", username);
    }

    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    @Transactional
    public Admin createAdmin(String username, String password, String email, String fullName) {
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        Admin admin = Admin.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .fullName(fullName)
                .isActive(true)
                .role("ROLE_ADMIN")
                .build();

        Admin savedAdmin = adminRepository.save(admin);
        log.info("Created new admin: {}", username);
        return savedAdmin;
    }

     @PostConstruct
     public void createDefaultAdmin() {
         if (!existsByUsername(defaultUsername)) {
             createAdmin(defaultUsername, defaultPassword, defaultEmail, defaultFullName);
             log.info("Default admin created with username: {}", defaultUsername);
         }
     }
}
