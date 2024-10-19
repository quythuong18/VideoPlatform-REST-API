package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.User.IUserRepository;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.AuthenticationResponse;
import com.qt.VideoPlatformAPI.Verification.EmailService;
import com.qt.VideoPlatformAPI.Verification.IUserVerificationRepository;
import com.qt.VideoPlatformAPI.Verification.OTPGenerator;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final IUserRepository userRepository;
    private final IUserVerificationRepository userVerificationRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public ResponseEntity<UserProfile> register(UserProfile userReq) {
        //hashing password
        userReq.setPassword(passwordEncoder.encode(userReq.getPassword()));
        // save user
        UserProfile user = userService.addUser(userReq).getBody();

        // call the verify account function here
        verifyAccount(userReq);

        return ResponseEntity.ok(user);
    }

    public AuthenticationResponse authenticate(UserProfile userReq) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(userReq.getUsername(), userReq.getPassword());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        System.out.println(userReq.getPassword());
//        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        Optional<UserProfile> user = userRepository.findByUsername(userReq.getUsername());

        if(user.isEmpty()) {
            throw new IllegalArgumentException("The username does not exist");
        }

        String token = jwtService.generateToken(user.get());
        return new AuthenticationResponse(Boolean.TRUE, "authenticated", HttpStatus.OK, token);
    }

    public ResponseEntity<APIResponse> verifyAccount(UserProfile userReq) {

        // Generating OTP and sending email task below can be done asynchronously
        CompletableFuture.runAsync(() -> {
            OTPGenerationAndSendEmail(userReq);
        }).thenAccept(System.out::println);

        return new ResponseEntity<>(new APIResponse(Boolean.TRUE,"OTP has been sent to your email!", HttpStatus.OK).getHttpStatus());
    }

    @Transactional
    public APIResponse otpVerification(UserVerification userVerificationReq) {
        Optional<UserVerification> userVerificationInDB = userVerificationRepository.findByUsername(userVerificationReq.getUser().getUsername());
        if(userVerificationInDB.isEmpty()) {
            return new APIResponse(Boolean.FALSE,"Account verification doesn't exist", HttpStatus.OK);
        }

        String requestOTP = userVerificationReq.getOtpCode();
        if(requestOTP.equals(userVerificationInDB.get().getOtpCode())) {
            if(userVerificationInDB.get().getExpirationTime().isBefore(Instant.now().minusSeconds(2))) {
                return new APIResponse(Boolean.FALSE,"OTP code expire", HttpStatus.OK);
            }
            // activate the account
            try {
                userRepository.activateAccount(userVerificationReq.getUser().getUsername());
                System.out.println("Account activated successfully");
            }
            catch (Exception e) {
                System.out.println("Account activation can not update in DB: " + e.getMessage());
                e.printStackTrace();
                return new APIResponse(Boolean.FALSE,"Account verified failed", HttpStatus.OK);
            }

            return new APIResponse(Boolean.TRUE,"Account verified successfully", HttpStatus.OK);
        }
        return new APIResponse(Boolean.FALSE,"OTP doesn't match", HttpStatus.OK);
    }

    @Transactional
    public void OTPGenerationAndSendEmail(UserProfile user) {

        //generate OTP and send it to user
        String otpVerification = OTPGenerator.generate(6);
        String textMessage = "This is OTP to activate your account\n" +
                            "It will expire in 60 seconds\n" +
                            otpVerification;

        System.out.println(textMessage);
        System.out.println("sending email...");
        emailService.send(user.getEmail(), "Account verification", textMessage);

        // save OTP to database and set time
        UserVerification userVerification = new UserVerification();
        userVerification.setUser(user);
        userVerification.setOtpCode(otpVerification);
        userVerification.setExpirationTime(Instant.now().plusSeconds(60));

//        userVerificationRepository.save(userVerification);
        try {
            userVerificationRepository.save(userVerification);
            System.out.println("OTP has been saved to DB");
        } catch (Exception e) {
            System.err.println("Error saving OTP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
