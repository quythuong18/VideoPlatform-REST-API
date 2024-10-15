package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Response.APIResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}/profile")
    ResponseEntity<UserProfile> getUserProfile(@PathVariable(value = "username") String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @GetMapping("/checkUsernameAvailability")
    APIResponse checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return userService.checkUsernameAvailability(username);
    }

    @GetMapping("/checkEmailAvailability")
    APIResponse checkEmailAvailability(@RequestParam(value = "email") String email) {
        return userService.checkEmailAvailability(email);
    }

    @PutMapping("/{username}")
    APIResponse updateUser(@RequestBody UserProfile user, @PathVariable(value = "username") String username) {
        return null;
    }

    @DeleteMapping("/{username}")
    ResponseEntity<UserProfile> deleteUser(@PathVariable(value = "username") String username) {
        return null;
    }
}
