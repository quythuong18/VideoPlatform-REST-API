package com.qt.VideoPlatformAPI.Playlist;

import com.qt.VideoPlatformAPI.Utils.VideoConstants;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import com.qt.VideoPlatformAPI.Video.IVideosRepository;
import com.qt.VideoPlatformAPI.Video.Video;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/playlists")
@AllArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    private final IPlaylistRepository iPlaylistRepository;
    private final IVideosRepository iVideosRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<APIResponseWithData<List<Playlist>>> getAllPlaylistsByUserId(@PathVariable Long userId) {
        if(userId == null)
            return ResponseEntity.status(400).body(new APIResponseWithData<>(Boolean.FALSE,
                    "userId is null", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<>(
                Boolean.TRUE, "Get all playlists of a user successfully", HttpStatus.OK,
                playlistService.getAllPlaylistsByUserId(userId)
        ));
    }

    @PostMapping("/")
    public ResponseEntity<APIResponseWithData<Playlist>> createPlaylist(@RequestBody Playlist playlist) {
        if(playlist == null)
            return ResponseEntity.status(400).body(new APIResponseWithData<Playlist>(Boolean.FALSE,
                    "Playlist is null", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<Playlist>(Boolean.TRUE,
                "Create playlist successfully", HttpStatus.OK, playlistService.createPlaylist(playlist)));
    }

    @PostMapping("/{playlistId}/thumbnail")
    public ResponseEntity<APIResponseWithData<String>> uploadPlaylistThumbnail(@RequestBody MultipartFile img,
    @PathVariable String playlistId) throws IOException {
        if(img == null)
            throw new IllegalArgumentException("Please upload a image file");
        if(!VideoConstants.IMAGE_MIME_TYPES.contains(img.getContentType()))
            throw new IllegalArgumentException("The image format is not supported");

        String profilePicUrl = playlistService.uploadPlaylistThumbnail(img, playlistId);
        return ResponseEntity.ok(new APIResponseWithData<String>(Boolean.TRUE,
                "Upload playlist thumbnail successfully", HttpStatus.OK, profilePicUrl));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<APIResponseWithData<Playlist>> getPlaylist(@PathVariable String playlistId) {
        if(playlistId == null || playlistId.isBlank())
            return ResponseEntity.status(400).body(new APIResponseWithData<Playlist>(Boolean.FALSE,
                    "Playlist id is null or blank", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<Playlist>(Boolean.TRUE,
                "Get a playlist successfully", HttpStatus.OK, playlistService.getPlaylistById(playlistId)));
    }

    @PatchMapping("/{playlistId}/add")
    public ResponseEntity<APIResponse> addVideoToPlaylist(@PathVariable String playlistId, @RequestParam String videoId) {
        if(playlistId == null || playlistId.isBlank())
            return ResponseEntity.status(402).body(new APIResponse(Boolean.FALSE, "Playlist id is null or blank", HttpStatus.BAD_REQUEST));
        if(videoId == null || videoId.isBlank())
            return ResponseEntity.status(400).body(new APIResponse(Boolean.FALSE, "Video id is null or blank", HttpStatus.BAD_REQUEST));

        playlistService.addVideoToPlaylist(playlistId, videoId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Add video to playlist successfully", HttpStatus.OK));
    }

    @PatchMapping("/{playlistId}/remove")
    public ResponseEntity<APIResponse> deleteVideoFromPlaylist(@PathVariable String playlistId, @RequestParam String videoId) {
        if(playlistId == null || playlistId.isBlank())
            return ResponseEntity.status(400).body(new APIResponse(Boolean.FALSE, "Playlist id is null or blank", HttpStatus.BAD_REQUEST));
        if(videoId == null || videoId.isBlank())
            return ResponseEntity.status(400).body(new APIResponse(Boolean.FALSE, "Video id is null or blank", HttpStatus.BAD_REQUEST));

        playlistService.deleteVideoFromPlaylist(playlistId, videoId, Boolean.FALSE);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Remove video from playlist successfully", HttpStatus.OK));
    }


}
