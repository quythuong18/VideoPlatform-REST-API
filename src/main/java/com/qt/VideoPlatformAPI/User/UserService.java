package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Responses.AvailabilityResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final IUserRepository userRepository;

    @Override
    public UserProfile loadUserByUsername(String username) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username can not be null or empty");
        }

        try {
            Optional<UserProfile> optionalUserProfile = userRepository.findByUsername(username);

            if(optionalUserProfile.isEmpty()) {
                throw new UsernameNotFoundException("The username does not exist");
            }
            UserProfile user = optionalUserProfile.get();
            return user;

        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user", e);
        }
    }

    public ResponseEntity<AvailabilityResponse> checkUsernameAvailability(String username) {
         Boolean isExisted = userRepository.existByUsername(username);
         if(isExisted)
             return ResponseEntity.ok(new AvailabilityResponse(Boolean.TRUE, "username " + username + " exists", HttpStatus.OK,Boolean.TRUE));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AvailabilityResponse(Boolean.FALSE, "username " + username + " does not exist", HttpStatus.NOT_FOUND, Boolean.FALSE));
    }

    public ResponseEntity<AvailabilityResponse> checkEmailAvailability(String email) {
        Boolean isExisted = userRepository.existByEmail(email);
        if(isExisted)
            return ResponseEntity.ok(new AvailabilityResponse(Boolean.TRUE, "email " + email + " exists", HttpStatus.OK,Boolean.TRUE));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AvailabilityResponse(Boolean.FALSE, "email " + email + " does not exist", HttpStatus.NOT_FOUND,Boolean.FALSE));
    }

}
