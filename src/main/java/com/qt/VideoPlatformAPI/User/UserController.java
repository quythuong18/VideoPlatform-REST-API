package com.qt.VideoPlatformAPI.User;

import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return null;
    }

    @PutMapping("/{username}")
    ResponseEntity<UserProfile> updateUser(@RequestBody UserProfile user, @PathVariable(value = "username") String username) {
        return null;
    }

    @DeleteMapping("/{username}")
    ResponseEntity<UserProfile> deleteUser(@PathVariable(value = "username") String username) {
        return null;
    }
}
