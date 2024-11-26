package com.qt.VideoPlatformAPI.Playlist;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PlaylistService {
    private final IPlaylistRepository iPlaylistRepository;
    private final UserService userService;
    private final VideoService videoService;

    public Playlist createPlaylist(Playlist playlist) {
        playlist.setUserId(userService.getCurrentUser().getId());
        return iPlaylistRepository.save(playlist);
    }

    public Playlist getPlaylistById(String playlistId) {
        Optional<Playlist> playlistOptional = iPlaylistRepository.findById(playlistId);
        if(playlistOptional.isEmpty())
            throw new IllegalArgumentException("Playlist id does not exist");
        return playlistOptional.get();
    }

    public void addVideoToPlaylist(String playlistId, String videoId) {
        if(!iPlaylistRepository.existsById(playlistId))
            throw new IllegalArgumentException("Playlist id does not exist");
        if(!videoService.isVideoExistent(videoId))
            throw new IllegalArgumentException("Video id does not exist");

        Playlist playlist = getPlaylistById(playlistId);
        playlist.getVideoIdsList().add(videoId);

        iPlaylistRepository.save(playlist);
    }

    public void deleteVideoFromPlaylist(String playlistId, String videoId) {
        if(!iPlaylistRepository.existsById(playlistId))
            throw new IllegalArgumentException("Playlist id does not exist");
        if(!videoService.isVideoExistent(videoId))
            throw new IllegalArgumentException("Video id does not exist");

        if(!checkVideoExistsInAPlaylist(playlistId, videoId))
            throw new IllegalArgumentException("The video is not in the playlist");

        Playlist playlist = getPlaylistById(playlistId);
        playlist.getVideoIdsList().remove(playlistId);
    }

    public Boolean checkVideoExistsInAPlaylist(String playlistId, String videoId) {
        Playlist playlist = getPlaylistById(playlistId);
        return playlist.getVideoIdsList().contains(videoId);
    }
}
