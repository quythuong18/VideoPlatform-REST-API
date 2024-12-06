package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<APIResponseWithData<List<Video>>> getAllVideosByUserId(@PathVariable Long userId) {
        if(userId == null)
            return ResponseEntity.status(400).body(new APIResponseWithData<>(Boolean.FALSE,
                    "UserId is null", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE,
                "Get all videos successfully", HttpStatus.OK, videoService.getAllVideosByUserId(userId)));
    }

    @GetMapping("/{videoid}") // get video metadata
    public ResponseEntity<APIResponseWithData<Video>> getOneVideo(@PathVariable(value = "videoid") String id) {
        return ResponseEntity.ok(new APIResponseWithData<>(true, "get one video metadata successfully",
        HttpStatus.OK, videoService.getVideoById(id)));
    }

    @GetMapping("/random")
    public ResponseEntity<APIResponseWithData<List<Video>>> getRandomVideos(@RequestParam Integer count) {
        if(count == null || count <= 0)
            return ResponseEntity.status(400).body(new APIResponseWithData<>(Boolean.FALSE, "Count value is invalid",
                    HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get random videos successfully",
                HttpStatus.OK, videoService.getRandomVideos(count)));
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponseWithData<?>> getSearchResult(
            @RequestParam(defaultValue = "title") String type,
            @RequestParam String pattern,
            @RequestParam(defaultValue = "6") Integer count
    ) {
        if(Objects.equals(type, "tag")) {
            return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get search result successfully", HttpStatus.OK,
                    videoService.searchByVideoTag(pattern, count)));
        }
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get search result successfully", HttpStatus.OK,
                videoService.searchByVideoTitle(pattern, count)));
    }

    @PostMapping("/{videoId}/thumbnail")
    public ResponseEntity<APIResponseWithData<String>> updateThumbnail(@PathVariable String videoId, @RequestBody MultipartFile file) throws IOException {
        if(file.isEmpty())
            throw new IllegalArgumentException("Please upload a image file");
        if(!videoService.isVideoExistent(videoId))
            throw new IllegalArgumentException("The video Id does not exist");

        String thumbnailUrl = videoService.uploadThumbnailVideo(videoId, file);

        return ResponseEntity.ok(new APIResponseWithData<String>(Boolean.TRUE,
        "Upload thumbnail successfully", HttpStatus.OK, thumbnailUrl));
    }
}
