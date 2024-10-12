package com.qt.VideoPlatformAPI.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserProfile, Long> {
    @Query("SELECT u FROM UserProfile u WHERE u.username = ?1")
    Optional<UserProfile> findByUsername(String username);

    @Query("SELECT p FROM UserProfile p WHERE p.email = ?1")
    Optional<UserProfile> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM UserProfile u WHERE u.email = ?1")
    Boolean existByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM UserProfile u WHERE u.username = ?1")
    Boolean existByUsername(String username);

    @Query("UPDATE UserProfile SET isVerified = true WHERE username = ?1")
    void activateAccount(String username);
}
