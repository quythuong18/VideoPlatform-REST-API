package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("api/v1/videos")
@AllArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/new/")
    public ResponseEntity<APIResponseWithData<Video>> addVideo(@RequestBody Video video) {
        return ResponseEntity.ok(new APIResponseWithData<>(true, "add video metadata successfully", HttpStatus.OK, videoService.addVideo(video)));
    }

    @GetMapping("/")
    public ResponseEntity<APIResponseWithData<List<Video>>> getVideos(Integer numberOfVideo) {
        return null;
    }

    @GetMapping("/{videoid}") // get video metadata
    public ResponseEntity<APIResponseWithData<Video>> getOneVideo(@PathVariable(value = "videoid") Long videoId) {
        return null;
    }

    @GetMapping("/{videoid}/watch")
    public ResponseEntity<APIResponseWithData<Video>> watchOneVideo(@PathVariable(value = "videoid") Long videoId) {
        return null;
    }
}