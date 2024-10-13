package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Verification.EmailService;
import com.qt.VideoPlatformAPI.Verification.IUserVerificationRepository;
import com.qt.VideoPlatformAPI.Verification.OTPGenerator;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class UserService {
    private final IUserRepository userRepository;
    private final IUserVerificationRepository userVerificationRepository;

    public UserProfile getUserProfile(String username) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username can not be null or empty");
        }

        Optional<UserProfile> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) {
            throw new IllegalArgumentException("The username does not exists");
        }
        return optionalUser.get();
    }

    public ResponseEntity<UserProfile> addUser(UserProfile user) {
        // Username check
        if(user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cant not be empty");
        }
        if(userRepository.existByUsername(user.getUsername())) {
            throw new IllegalArgumentException("The username exists");
        }

        // Email check
        if(user.getEmail().isEmpty()){
            throw new IllegalArgumentException("Email cant not be empty");
        }
        if(userRepository.existByEmail(user.getEmail())) {
            throw new IllegalArgumentException("The email exists");
        }

        user.setIsVerified(Boolean.FALSE);
        user.setIsPrivate(Boolean.FALSE);

        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }

}
