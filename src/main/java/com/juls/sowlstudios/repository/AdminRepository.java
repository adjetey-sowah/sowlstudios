package com.juls.sowlstudios.repository;

import com.juls.sowlstudios.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Admin a SET a.lastLogin = :lastLogin WHERE a.username = :username")
    void updateLastLogin(@Param("username") String username, @Param("lastLogin") LocalDateTime lastLogin);

    @Query("SELECT COUNT(a) FROM Admin a WHERE a.isActive = true")
    Long countActiveAdmins();
}
