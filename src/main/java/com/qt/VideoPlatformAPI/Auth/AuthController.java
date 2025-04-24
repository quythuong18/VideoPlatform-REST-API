package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponseWithData<UserProfile>>  register(@RequestBody UserProfile userReq) {
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE,
        "Register successfully", HttpStatus.OK, authService.register(userReq)));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<APIResponseWithData<SignInTokens>> signIn(@RequestBody UserProfile userReq) {
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE,
                "authenticated", HttpStatus.OK, authService.authenticate(userReq)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<APIResponseWithData<SignInTokens>> refresh(@RequestBody UserProfile userReq,
            @RequestHeader String rt) {

        if(rt == null || rt.isBlank())
            throw new IllegalArgumentException("Bad tokens");
        SignInTokens tokens = new SignInTokens();
        tokens.setAccessToken(authService.getNewAccessToken(userReq, rt));
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE,
                "authenticated", HttpStatus.OK, tokens));
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
