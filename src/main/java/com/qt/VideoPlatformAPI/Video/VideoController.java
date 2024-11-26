package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<APIResponseWithData<List<Video>>> getVideos(Integer numberOfVideo) {
        return null;
    }

    @GetMapping("/{videoid}") // get video metadata
    public ResponseEntity<APIResponseWithData<Video>> getOneVideo(@PathVariable(value = "videoid") String id) {
        return ResponseEntity.ok(new APIResponseWithData<>(true, "get one video metadata successfully",
        HttpStatus.OK, videoService.getVideoById(id)));
    }

    @GetMapping("/{videoid}/watch")
    public ResponseEntity<APIResponseWithData<Video>> watchOneVideo(@PathVariable(value = "videoid") String videoId) {
        return null;
    }
}
