package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Responses.AvailabilityResponse;
import com.qt.VideoPlatformAPI.Verification.IUserVerificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public AvailabilityResponse checkUsernameAvailability(String username) {
         Boolean isExisted = userRepository.existByUsername(username);
         if(isExisted)
             return new AvailabilityResponse(Boolean.TRUE, "username " + username + " exists", HttpStatus.OK,Boolean.TRUE);
        return new AvailabilityResponse(Boolean.TRUE, "username " + username + " does not exist", HttpStatus.OK,Boolean.FALSE);
    }

    public AvailabilityResponse checkEmailAvailability(String email) {
        Boolean isExisted = userRepository.existByEmail(email);
        if(isExisted)
            return new AvailabilityResponse(Boolean.TRUE, "email " + email + " exists", HttpStatus.OK,Boolean.TRUE);
        return new AvailabilityResponse(Boolean.TRUE, "email " + email + " does not exist", HttpStatus.OK,Boolean.FALSE);
    }
}
