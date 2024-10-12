package com.qt.VideoPlatformAPI.Verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserVerificationRepository extends JpaRepository<UserVerification, Long> {
    @Query("SELECT u FROM UserVerification u WHERE u.user.username = ?1 ORDER BY u.id DESC LIMIT 1")
    Optional<UserVerification> findByUsername(String username);
}
