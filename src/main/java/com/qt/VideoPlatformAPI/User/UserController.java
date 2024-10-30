package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.Responses.AvailabilityResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}/profile")
    ResponseEntity<APIResponseWithData<UserProfile>> getCurrentUserProfile(@PathVariable(name = "username") String username) {
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "get profile successfully", HttpStatus.OK, userService.loadUserByUsername(username)));
    }

    @GetMapping("/checkUsernameAvailability")
    ResponseEntity<AvailabilityResponse> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return userService.checkUsernameAvailability(username);
    }

    @GetMapping("/checkEmailAvailability")
    ResponseEntity<AvailabilityResponse> checkEmailAvailability(@RequestParam(value = "email") String email) {
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
