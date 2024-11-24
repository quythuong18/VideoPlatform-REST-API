package com.qt.VideoPlatformAPI.Video.Like;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos/likes")
@AllArgsConstructor
public class LikeController {
    private final LikeService likeService;
    @PostMapping("/{videoId}")
    public ResponseEntity<APIResponse> likeVideo(@PathVariable String videoId) {
        if(videoId == null || videoId.isEmpty())
            return ResponseEntity.status(400).body(
                    new APIResponse(Boolean.FALSE, "Video id is null or empty", HttpStatus.BAD_REQUEST));
        likeService.LikeVideo(videoId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Like video successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<APIResponse> removeLikeVideo(@PathVariable String videoId) {
        if(videoId == null || videoId.isEmpty())
            return ResponseEntity.status(400).body(
                    new APIResponse(Boolean.FALSE, "Video id is null or empty", HttpStatus.BAD_REQUEST));
        likeService.removeLikeVideo(videoId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Unlike video successfully", HttpStatus.OK));
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<APIResponse> checkLikeVideo(@PathVariable String videoId) {
        if(likeService.checkLikeVideo(videoId))
            return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Liked", HttpStatus.OK));
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Not like yet", HttpStatus.OK));
    }
}
