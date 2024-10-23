package com.qt.VideoPlatformAPI.Video;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
public class Video {
    @Id
    private Long id;

    @Column
    private String title;

    @Column
    private String desc;

    @Column
    private List<String> tags;

    @Column
    private String url;

    @Column
    private Long duration;

    @Column
    private Instant uploadDate;

    @Column
    private Long viewsCount;

    @Column
    private Long likesCount;

    @Column
    private Boolean isPrivate;
}
