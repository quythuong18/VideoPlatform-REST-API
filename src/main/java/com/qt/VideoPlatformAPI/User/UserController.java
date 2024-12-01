package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Config.VideoEnv;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.Responses.AvailabilityResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    ResponseEntity<APIResponseWithData<UserProfile>> getCurrentUserProfile(@PathVariable(name = "username") String username) {
        UserProfile user = userService.loadUserByUsername(username);
        user.setPassword(null);
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "get profile successfully", HttpStatus.OK, user));
    }

    @GetMapping("/checkUsernameAvailability")
    ResponseEntity<AvailabilityResponse> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return userService.checkUsernameAvailability(username);
    }

    @GetMapping("/checkEmailAvailability")
    ResponseEntity<AvailabilityResponse> checkEmailAvailability(@RequestParam(value = "email") String email) {
        return userService.checkEmailAvailability(email);
    }

    @PostMapping("/{username}/follow")
    ResponseEntity<APIResponse> followAUser(@PathVariable(value = "username") String username) {
        return ResponseEntity.ok(userService.followAUser(username));
    }

    @PostMapping("/{username}/unfollow")
    ResponseEntity<APIResponse> unfollowAUser(@PathVariable(value = "username") String username) {
        return ResponseEntity.ok(userService.unfollowAUser(username));
    }

    @GetMapping("/followings")
    public ResponseEntity<APIResponseWithData<Set<String>>> getAllFollowings() {
        return ResponseEntity.ok(new APIResponseWithData<Set<String>>(Boolean.TRUE,
                "Get all followings successfully", HttpStatus.OK, userService.getAllFollowings()));
    }

    @PutMapping("/")
    ResponseEntity<APIResponseWithData<UserProfile>> updateUser(@RequestBody UserProfile user) {
        if(user == null)
            return ResponseEntity.status(400).body(new APIResponseWithData<UserProfile>(Boolean.FALSE, "User is null", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE,
                "Update user successfully", HttpStatus.OK, userService.updateUser(user)));
    }

    @PostMapping("/profilePic")
    ResponseEntity<APIResponseWithData<String>> updateProfilePic(@RequestBody MultipartFile img) throws IOException {
        if(img == null)
            throw new IllegalArgumentException("Please upload a image file");
        if(!VideoEnv.IMAGE_MIME_TYPES.contains(img.getContentType()))
            throw new IllegalArgumentException("The image format is not supported");

        String profilePicUrl = userService.updateProfilePic(img);
        return ResponseEntity.ok(new APIResponseWithData<String>(Boolean.TRUE,
                "Upload profile picture successfully", HttpStatus.OK, profilePicUrl));
    }
}
