package com.qt.VideoPlatformAPI.Playlist;

import com.qt.VideoPlatformAPI.File.CloudinaryService;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PlaylistService {
    private final IPlaylistRepository iPlaylistRepository;
    private final UserService userService;
    private final VideoService videoService;
    private final CloudinaryService cloudinaryService;

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

    public String uploadPlaylistThumbnail(MultipartFile img, String playlistId) throws IOException {
        Playlist playlist = getPlaylistById(playlistId);
        String url;
        if(playlist.getPictureUrl() == null || playlist.getPictureUrl().isBlank())
            url = cloudinaryService.uploadPhoto(img, "profile", playlistId);
        else
            url = cloudinaryService.updatePhoto(img, "profile", playlistId);
        playlist.setPictureUrl(url);
        iPlaylistRepository.save(playlist);
        return url;
    }

    public List<Playlist> getAllPlaylistsByUserId(Long userId) {
        List<Playlist> allPlaylists = iPlaylistRepository.findAllByUserId(userId);
        return allPlaylists;
    }

    public void addVideoToPlaylist(String playlistId, String videoId) {
        if(!iPlaylistRepository.existsById(playlistId))
            throw new IllegalArgumentException("Playlist id does not exist");
        if(!videoService.isVideoExistent(videoId))
            throw new IllegalArgumentException("Video id does not exist");

        if(checkVideoExistsInAPlaylist(playlistId, videoId))
            throw new IllegalArgumentException("The video is already added to playlist");

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
        playlist.getVideoIdsList().remove(videoId);

        iPlaylistRepository.save(playlist);
    }

    public Boolean checkVideoExistsInAPlaylist(String playlistId, String videoId) {
        Playlist playlist = getPlaylistById(playlistId);
        return playlist.getVideoIdsList().contains(videoId);
    }
}
