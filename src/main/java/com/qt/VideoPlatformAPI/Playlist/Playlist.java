package com.qt.VideoPlatformAPI.Playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("playlists")
@Getter
@Setter
@AllArgsConstructor
public class Playlist {
    private String id;
    private Long userId;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private String pictureUrl;
    @NotNull(message = "Video id list must be non-null")
    private List<String> videoIdsList;
}
