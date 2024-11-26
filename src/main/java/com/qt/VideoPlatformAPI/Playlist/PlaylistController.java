package com.qt.VideoPlatformAPI.Playlist;

import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Responses.APIResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/playlists")
@AllArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    @PostMapping
    public ResponseEntity<APIResponseWithData<Playlist>> createPlaylist(@RequestBody Playlist playlist) {
        if(playlist == null)
            return ResponseEntity.status(400).body(new APIResponseWithData<Playlist>(Boolean.FALSE,
                    "Playlist is null", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<Playlist>(Boolean.TRUE,
                "Create playlist successfully", HttpStatus.OK, playlistService.createPlaylist(playlist)));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<APIResponseWithData<Playlist>> getPlaylist(@PathVariable String playlistId) {
        if(playlistId == null || playlistId.isBlank())
            return ResponseEntity.status(400).body(new APIResponseWithData<Playlist>(Boolean.FALSE,
                    "Playlist id is null or blank", HttpStatus.BAD_REQUEST, null));
        return ResponseEntity.ok(new APIResponseWithData<Playlist>(Boolean.TRUE,
                "Create playlist successfully", HttpStatus.OK, playlistService.getPlaylistById(playlistId)));
    }

    @PatchMapping("/{playlistId}")
    public ResponseEntity<APIResponse> addVideoToPlaylist(@PathVariable String playlistId, @RequestParam String videoId) {
        if(playlistId == null || playlistId.isBlank())
            return ResponseEntity.status(400).body(new APIResponse(Boolean.FALSE, "Playlist id is null or blank", HttpStatus.BAD_REQUEST));
        if(videoId == null || videoId.isBlank())
            return ResponseEntity.status(400).body(new APIResponse(Boolean.FALSE, "Video id is null or blank", HttpStatus.BAD_REQUEST));

        playlistService.addVideoToPlaylist(playlistId, videoId);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Add video to playlist successfully", HttpStatus.OK));
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<APIResponse> deleteVideoFromPlaylist(@PathVariable String playlistId, @RequestParam String videoId) {
        return null;
    }


}
