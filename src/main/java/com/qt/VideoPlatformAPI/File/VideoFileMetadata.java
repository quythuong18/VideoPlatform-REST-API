package com.qt.VideoPlatformAPI.File;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VideoFileMetadata {
    private String Id;
    private String pathName;

    // video
    private Integer width;
    private Integer height;
    private Float fps;
    private Long videoBitrate;
    private String videoCodec;

    // audio
    private Long audioBitrate;
    private String audioCodec;

    private String format;
    private List<Integer> qualities = new ArrayList<>();
}
