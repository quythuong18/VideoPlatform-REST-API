package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("api/v1/videos")
public class VideoController {
    @GetMapping("/")
    public ResponseEntity<APIResponseWithData<List<Video>>> getVideos(Integer numberOfVideo) {
        return null;
    }

    @GetMapping("/{videoid}")
    public ResponseEntity<APIResponseWithData<Video>> getOneVideo(@PathVariable(value = "videoid") Long videoId) {
        return null;
    }

    @GetMapping("/{videoid}/watch")
    public ResponseEntity<APIResponseWithData<Video>> watchOneVideo(@PathVariable(value = "videoid") Long videoId) {
        return null;
    }

    @PostMapping("/upload")
    public ResponseEntity<APIResponse> uploadVideo(@RequestBody MultipartFile videoFile) {
        return null;
    }
}
