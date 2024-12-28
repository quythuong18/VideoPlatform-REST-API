package com.qt.VideoPlatformAPI;

import com.qt.VideoPlatformAPI.File.CloudinaryService;
import com.qt.VideoPlatformAPI.Playlist.IPlaylistRepository;
import com.qt.VideoPlatformAPI.Playlist.Playlist;
import com.qt.VideoPlatformAPI.Playlist.PlaylistService;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.IVideosRepository;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@SpringBootTest
@AutoConfigureMockMvc
class VideoPlatformApiApplicationTests {
    private MockMvc mockMvc;

    @Mock
    private IPlaylistRepository iPlaylistRepository;
    @Mock
    private IVideosRepository iVideosRepository;

    @Test
    void updatePlaylistIdField() {
        List<Playlist> playlists = iPlaylistRepository.findAll();
        for(Playlist p : playlists) {
            List<String> videoIdList = p.getVideoIdsList();
            for(String videoId : videoIdList) {
                Optional<Video> videoOptional = iVideosRepository.findById(videoId);
                if(videoOptional.isPresent()) {
                    Video video = videoOptional.get();
                    video.setPlaylistId(p.getId());
                }
            }
        }
    }
}
