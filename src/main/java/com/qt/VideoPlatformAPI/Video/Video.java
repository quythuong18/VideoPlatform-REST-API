package com.qt.VideoPlatformAPI.Video;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("video")
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Video {
    @Id
    private String id;
    private Long userId;
    private String title;
    private String description;
    private List<String> tags;
    private String url;
    private Long duration;
    private Instant uploadDate;
    private Long viewsCount;
    private Long likesCount;
    private Boolean isPrivate;
}
