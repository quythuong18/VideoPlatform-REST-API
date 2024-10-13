package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.User.IUserRepository;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Utils.APIResponse;
import com.qt.VideoPlatformAPI.Utils.AuthenticationResponse;
import com.qt.VideoPlatformAPI.Verification.IUserVerificationRepository;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final IUserRepository userRepository;
    private final IUserVerificationRepository UserVerificationRepository;

    public ResponseEntity<UserProfile> register(UserProfile userProfileReq) {
        //hashing password
        userProfileReq.setPassword(passwordEncoder.encode(userProfileReq.getPassword()));
        // save user
        UserProfile user = userService.addUser(userProfileReq);
        return ResponseEntity.ok(user);
    }

    public AuthenticationResponse signIn(UserProfile userProfileReq) {
        return null;
    }

    public ResponseEntity<APIResponse> otpVerification(UserVerification userVerificationReq) {
        Optional<UserVerification> userVerification = UserVerificationRepository.findByUsername(userVerificationReq.getUser().getUsername());
        if(userVerification.isEmpty()) {
            throw new IllegalArgumentException("Account doesn't exist");
        }

        String requestOTP = userVerificationReq.getOtpCode();
        if(requestOTP.equals(userVerification.get().getOtpCode())) {
            // activate the account
            userRepository.activateAccount(userVerificationReq.getUser().getUsername());
        }
        return new ResponseEntity<>(new APIResponse(Boolean.TRUE,"Account verified successfully", HttpStatus.OK).getHttpStatus());
    }
}
