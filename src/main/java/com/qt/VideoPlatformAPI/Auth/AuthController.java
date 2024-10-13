package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Utils.APIResponse;
import com.qt.VideoPlatformAPI.Utils.AuthenticationResponse;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserProfile>  register(@RequestBody UserProfile userReq) {
        return authService.register(userReq);
    }

    @PostMapping("/signIn")
    public AuthenticationResponse  signIn() {
        return new AuthenticationResponse("testing message");
    }

    @PostMapping("/OTPVerification")
    public APIResponse otpVerification(@RequestBody UserVerification userVerificationReq) {
        return authService.otpVerification(userVerificationReq);
    }

    @PostMapping("/VerifyAccount")
    public ResponseEntity<APIResponse> verifyAccount(@RequestBody UserProfile userReq) {
        return authService.verifyAccount(userReq);
    }
}
