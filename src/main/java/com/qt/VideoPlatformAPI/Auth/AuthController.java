package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.AuthenticationResponse;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponseWithData<UserProfile>>  register(@RequestBody UserProfile userReq) {
        return authService.register(userReq);
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody UserProfile userReq) {
        return authService.authenticate(userReq);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse> resetPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.resetPassword(email));
    }

    @PostMapping("/reset-password-verification")
    public ResponseEntity<APIResponse> resetPasswordVerification(@RequestBody UserVerification userVerificationReq) {
        return ResponseEntity.ok(authService.resetPasswordVerification(userVerificationReq));
    }

    @PostMapping("/account-otp-verification")
    public ResponseEntity<APIResponse> otpVerification(@RequestBody UserVerification userVerificationReq) {
        return ResponseEntity.ok(authService.activateAccount(userVerificationReq));
    }

    @PostMapping("/verify-account")
    public ResponseEntity<APIResponse> verifyAccount(@RequestBody UserProfile userReq) {
        return authService.verifyAccountAsync(userReq);
    }
}
