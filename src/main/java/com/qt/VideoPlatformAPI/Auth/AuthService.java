package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.User.IUserRepository;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.AuthenticationResponse;
import com.qt.VideoPlatformAPI.Verification.EmailService;
import com.qt.VideoPlatformAPI.Verification.IUserVerificationRepository;
import com.qt.VideoPlatformAPI.Verification.OTPGenerator;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IUserVerificationRepository userVerificationRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public ResponseEntity<APIResponseWithData<UserProfile>> register(UserProfile userReq) {

        //hashing password
        userReq.setPassword(passwordEncoder.encode(userReq.getPassword()));
        // Username check
        if(userReq.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cant not be empty");
        }
        if(userRepository.existByUsername(userReq.getUsername())) {
            throw new IllegalArgumentException("The username exists");
        }

        // Email check
        if(userReq.getEmail().isEmpty()){
            throw new IllegalArgumentException("Email cant not be empty");
        }
        if(userRepository.existByEmail(userReq.getEmail())) {
            throw new IllegalArgumentException("The email exists");
        }

        userReq.setIsVerified(Boolean.FALSE);
        userReq.setIsPrivate(Boolean.FALSE);
        UserProfile user = userRepository.save(userReq);

        // call the verify account function here
        verifyAccount(userReq);

        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Register successfully", HttpStatus.OK, user));
    }

    public ResponseEntity<AuthenticationResponse> authenticate(UserProfile userReq) {
        // find the user in db
        Optional<UserProfile> user = userRepository.findByUsername(userReq.getUsername());
        if(user.isEmpty()) {
            throw new IllegalArgumentException("The username does not exist");
        }

        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
            = new UsernamePasswordAuthenticationToken(userReq.getUsername(), userReq.getPassword(), null);
            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }
        catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse(Boolean.FALSE, "Invalid username or password", HttpStatus.UNAUTHORIZED, null));
        }

        // generate toke to send
        String token = jwtService.generateToken(user.get());
        return ResponseEntity.ok(new AuthenticationResponse(Boolean.TRUE, "authenticated", HttpStatus.OK, token));
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
                return new APIResponse(Boolean.FALSE,"Account verified failed", HttpStatus.UNAUTHORIZED);
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
