package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Utils.AuthenticationResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    public ResponseEntity<UserProfile> register(UserProfile userProfileReq) {
        UserProfile user = userService.addUser(userProfileReq);
        return ResponseEntity.ok(user);
    }

    public AuthenticationResponse signIn(UserProfile userProfileReq) {
        return null;
    }
}
