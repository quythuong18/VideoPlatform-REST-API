package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.Playlist.PlaylistService;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.View.ViewHistory;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/videos")
@AllArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final PlaylistService playlistService;
    private final UserService userService;

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

    @GetMapping("/following/random")
    public ResponseEntity<APIResponseWithData<List<Video>>> getRandomVideosOfFollowings(@RequestParam Integer count) {
        if(count == null || count <= 0)
            return ResponseEntity.status(400).body(new APIResponseWithData<>(Boolean.FALSE, "Count value is invalid",
                    HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get random videos of your followings successfully",
                HttpStatus.OK, videoService.getRandomVideosOfFollowings(count)));
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

    @GetMapping("/liked")
    public ResponseEntity<APIResponseWithData<List<Video>>> getAllLikedVideos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get liked videos successfully", HttpStatus.OK,
                videoService.getAllLikedVideos(page, size)));
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

    @PutMapping("")
    public ResponseEntity<APIResponseWithData<Video>> updateVideo(@RequestBody Video video) {
        if(video == null)
            throw new IllegalArgumentException("Video object is null");
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Update video successfully", HttpStatus.OK,
                videoService.updateVideoInfo(video)));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<APIResponse> deleteVideo(@PathVariable String videoId) {
        if(videoId == null || videoId.isBlank())
            throw new IllegalArgumentException("Video id is null or blank");
        videoService.deleteVideo(videoId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Delete video successfully", HttpStatus.OK));
    }

    @GetMapping("/{videoId}/view")
    public ResponseEntity<APIResponse> increaseView(@PathVariable String videoId) {
        if(videoId == null || videoId.isBlank()) {
            throw new IllegalArgumentException("Video id is null or blank");
        }
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, videoService.increaseView(videoId).toString(),
                HttpStatus.OK));
    }

    @PostMapping("/history")
    public ResponseEntity<APIResponse> viewHistoryupdate(@RequestBody ViewHistory vh) {
        if(vh == null) {
            throw new IllegalArgumentException("View history is null");
        }
        videoService.addHistory(vh);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "View history saved",
                HttpStatus.OK));
    }

    @GetMapping("/history")
    public ResponseEntity<APIResponseWithData<List<ViewHistory>>> getViewHistories(@RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        UserProfile user = userService.getCurrentUser();
        List<ViewHistory> viewHistories = videoService.getViewHistories(user, page, size);
        return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get view histories successfully", HttpStatus.OK,
                viewHistories));
    }

    @PatchMapping("/{videoId}/playlists")
    public ResponseEntity<APIResponse> addVideoToMultiplePlaylists(@RequestBody List<String> playlistIds,
        @PathVariable String videoId) {
        if (playlistIds == null || playlistIds.isEmpty())
            throw new IllegalArgumentException("Playlist id list is null or empty");

        // remove duplicated
        playlistIds = new ArrayList<>(new LinkedHashSet<>(playlistIds));
        // loop to add video to playlist
        for (String playlistId : playlistIds) playlistService.addVideoToPlaylist(playlistId, videoId);

        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Add video to multiple playlists successfully",
                HttpStatus.OK));
    }
}
