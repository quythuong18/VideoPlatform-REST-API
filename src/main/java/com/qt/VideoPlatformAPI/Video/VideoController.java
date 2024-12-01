package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/videos")
@AllArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/new/")
    public ResponseEntity<APIResponseWithData<Video>> addVideo(@RequestBody Video video) {
        return ResponseEntity.ok(new APIResponseWithData<>(true, "add video metadata successfully",
        HttpStatus.OK, videoService.addVideo(video)));
    }

    @GetMapping("/")
    public ResponseEntity<APIResponseWithData<List<Video>>> getAllVideosByUsername(@RequestParam String username) {
        if(username == null || username.isBlank())
            return ResponseEntity.status(400).body(new APIResponseWithData<>(Boolean.FALSE,
                    "Username is null or blank", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<List<Video>>(Boolean.TRUE,
                "Get all videos successfully", HttpStatus.OK, videoService.getAllVideosByUsername(username)));
    }

    @GetMapping("/{videoid}") // get video metadata
    public ResponseEntity<APIResponseWithData<Video>> getOneVideo(@PathVariable(value = "videoid") String id) {
        return ResponseEntity.ok(new APIResponseWithData<>(true, "get one video metadata successfully",
        HttpStatus.OK, videoService.getVideoById(id)));
    }

    @PostMapping("/{videoId}/thumbnail")
    public ResponseEntity<APIResponseWithData<String>> updateThumbnail(@PathVariable String videoId, @RequestBody MultipartFile file) throws IOException {
        if(file.isEmpty())
            throw new IllegalArgumentException("Please upload a image file");
        if(!videoService.isVideoExistent(videoId))
            throw new IllegalArgumentException("The video Id does not exist");

        String thumbnailUrl = videoService.updateThumbnailVideo(videoId, file);

        return ResponseEntity.ok(new APIResponseWithData<String>(Boolean.TRUE,
        "Upload thumbnail successfully", HttpStatus.OK, thumbnailUrl));
    }
}
