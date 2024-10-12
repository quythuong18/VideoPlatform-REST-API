package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Utils.APIResponse;
import com.qt.VideoPlatformAPI.Verification.EmailService;
import com.qt.VideoPlatformAPI.Verification.IUserVerificationRepository;
import com.qt.VideoPlatformAPI.Verification.OTPGenerator;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserService {
    private final IUserRepository iUserRepository;
    private final IUserVerificationRepository iUserVerificationRepository;
    private final EmailService emailService;

    public UserProfile getUserProfile(String username) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username can not be null or empty");
        }

        Optional<UserProfile> optionalUser = iUserRepository.findByUsername(username);
        if(optionalUser.isEmpty()) {
            throw new IllegalArgumentException("The username does not exists");
        }
        return optionalUser.get();
    }

    public UserProfile addUser(UserProfile user) {
        // Username check
        if(user.getUsername().isEmpty())
            throw new IllegalArgumentException("Username cant not be empty");
        if(iUserRepository.existByUsername(user.getUsername()))
            throw new IllegalArgumentException("The username exists");

        // Email check
        if(user.getEmail().isEmpty())
            throw new IllegalArgumentException("Email cant not be empty");
        if(iUserRepository.existByEmail(user.getEmail()))
            throw new IllegalArgumentException("The email exists");

        user.setIsVerified(Boolean.FALSE);
        user.setIsPrivate(Boolean.FALSE);

        UserProfile userProfile = null;
        try {
            userProfile = iUserRepository.save(user);
            OTPGeneration(user);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return userProfile;
    }

    public ResponseEntity<APIResponse> verifyUserAccount(UserVerification userVerificationReq) {
        Optional<UserVerification> userVerification = iUserVerificationRepository.findByUsername(userVerificationReq.getUser().getUsername());
        if(userVerification.isEmpty()) {
            throw new IllegalArgumentException("Account doesn't exist");
        }

        String requestOTP = userVerificationReq.getOtpCode();
        if(requestOTP.equals(userVerification.get().getOtpCode())) {
            // activate the account
            iUserRepository.activateAccount(userVerificationReq.getUser().getUsername());
        }
            return new ResponseEntity<>(new APIResponse(Boolean.TRUE,"Account verified successfully", HttpStatus.OK).getHttpStatus());
    }

    public void OTPGeneration(UserProfile user) {

        //generate OTP and send it to user
        String otpVerification = OTPGenerator.generate(6);
        emailService.send(user.getEmail(), "Account verification", otpVerification);

        // save OTP to database and set time
        UserVerification userVerification = new UserVerification();
        userVerification.setUser(user);
        userVerification.setOtpCode(otpVerification);
        userVerification.setExpirationTime(Instant.now().plusSeconds(60));

        iUserVerificationRepository.save(userVerification);
    }
}
