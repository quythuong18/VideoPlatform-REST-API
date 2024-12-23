package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.User.IUserRepository;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.AuthenticationResponse;
import com.qt.VideoPlatformAPI.Utils.EmailService;
import com.qt.VideoPlatformAPI.Verification.IUserVerificationRepository;
import com.qt.VideoPlatformAPI.Verification.OTPGenerator;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        userReq.setFollowerCount(0L);
        userReq.setFollowingCount(0L);
        UserProfile user = userRepository.save(userReq);

        // call the verify account function here
        verifyAccountAsync(userReq);

        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Register successfully", HttpStatus.OK, user));
    }

    @Transactional
    public APIResponse activateAccount(UserVerification userVerification) {
        if(otpVerification(userVerification)) {
            try {
                userRepository.activateAccount(userVerification.getUser().getUsername());
                return new APIResponse(Boolean.TRUE,"Account activated successfully", HttpStatus.OK);
            }
            catch (Exception e) {
                System.out.println("Account verification can not update in DB: " + e.getMessage());
                e.printStackTrace();
                return new APIResponse(Boolean.FALSE,"Account activated failed", HttpStatus.BAD_REQUEST);
            }
        }
        return new APIResponse(Boolean.FALSE,"Account activated failed", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<AuthenticationResponse> authenticate(UserProfile userReq) {
        // find the user in db
        Optional<UserProfile> user = userRepository.findByUsername(userReq.getUsername());
        if(user.isEmpty()) {
            throw new UsernameNotFoundException("The username does not exist");
        }

        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
            = new UsernamePasswordAuthenticationToken(userReq.getUsername(), userReq.getPassword(), null);
            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }
        catch (AuthenticationException e) {
            throw new UsernameNotFoundException("Invalid password");
        }

        // if the account has not been verified yet
        if(!user.get().getIsVerified()) {
            verifyAccountAsync(user.get());
            throw new UsernameNotFoundException("Verifying you account, please check you email inbox!");
        }

        // generate toke to send
        String token = jwtService.generateToken(user.get());
        return ResponseEntity.ok(new AuthenticationResponse(Boolean.TRUE, "authenticated", HttpStatus.OK, token));
    }

    public APIResponse resetPassword(String email) {
        Optional<UserProfile> userProfileOptional = userRepository.findByEmail(email);
        if(userProfileOptional.isEmpty())
            return new APIResponse(Boolean.FALSE, "User with that email not found", HttpStatus.NOT_FOUND);
        verifyAccountAsync(userProfileOptional.get());
        return new APIResponse(Boolean.TRUE, "An OTP was sent to your email to update your new password", HttpStatus.OK);
    }

    @Transactional
    public APIResponse resetPasswordVerification(UserVerification userVerificationReq) {
        if(userVerificationReq.getUser().getPassword() == null || userVerificationReq.getUser().getPassword().isEmpty())
            throw new IllegalArgumentException("Password can not be null or empty");
        if(userVerificationReq.getUser().getEmail() == null || userVerificationReq.getUser().getEmail().isEmpty())
            throw new IllegalArgumentException("Email can not be emtpy or null");
//        if(userVerificationReq.getUser().getUsername() == null || userVerificationReq.getUser().getUsername().isEmpty())
//            throw new IllegalArgumentException("User name can not be emtpy or null");

        if(otpVerification(userVerificationReq)) {
            try {
                Optional<UserProfile> userProfileOptional = userRepository.findByEmail(userVerificationReq.getUser().getEmail());
                if(userProfileOptional.isEmpty())
                    throw new IllegalArgumentException("User with email not found");
                UserProfile userProfile = userProfileOptional.get();
                // update new password
                userProfile.setPassword(passwordEncoder.encode(userVerificationReq.getUser().getPassword()));

                userRepository.save(userProfile);
                return new APIResponse(Boolean.TRUE, "Reset password successfully", HttpStatus.OK);
            } catch(Exception e) {
                return new APIResponse(Boolean.FALSE, "Reset password fail", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new APIResponse(Boolean.FALSE, "Reset password fail", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<APIResponse> verifyAccountAsync(UserProfile userReq) {

        // Generating OTP and sending email task below can be done asynchronously
        CompletableFuture.runAsync(() -> {
            OTPGenerationAndSendEmail(userReq);
        }).thenAccept(System.out::println);

        return new ResponseEntity<>(new APIResponse(Boolean.TRUE,"OTP has been sent to your email!", HttpStatus.OK).getHttpStatus());
    }

    public Boolean otpVerification(UserVerification userVerificationReq) {
//        Optional<UserVerification> userVerificationInDB = userVerificationRepository.findByUsername(userVerificationReq.getUser().getUsername());
        Optional<UserVerification> userVerificationInDB = userVerificationRepository.findByEmail(userVerificationReq.getUser().getEmail());
        if(userVerificationInDB.isEmpty()) {
            throw new IllegalArgumentException("Account verification doesn't exist");
        }

        String requestOTP = userVerificationReq.getOtpCode();
        if(requestOTP.equals(userVerificationInDB.get().getOtpCode())) {
            if(userVerificationInDB.get().getExpirationTime().isBefore(Instant.now().minusSeconds(2))) {
                throw new IllegalArgumentException("OTP code expire");
            }
            return true;
        }
        return false;
    }

    @Transactional
    public void OTPGenerationAndSendEmail(UserProfile user) {

        //generate OTP and send it to user
        String otpVerification = OTPGenerator.generate(6);
        String textMessage = "This is OTP to activate your account\n" +
                            "It will expire in 150 seconds\n" +
                            otpVerification;

        System.out.println(textMessage);
        System.out.println("sending email...");
        emailService.send(user.getEmail(), "Account verification", textMessage);

        // save OTP to database and set time
        UserVerification userVerification = new UserVerification();
        userVerification.setUser(user);
        userVerification.setOtpCode(otpVerification);
        userVerification.setExpirationTime(Instant.now().plusSeconds(120));

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
