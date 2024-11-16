package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.File.storage.FileSystemStorageService;
import com.qt.VideoPlatformAPI.Utils.VideoEnv;
import lombok.AllArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FFmpegService {
    FFmpeg ffmpeg;
    FFprobe ffprobe;

    public VideoFileMetadata getVideoFileMetadata(String videoId) throws IOException {
        String videoFileName = getVideoFileName(videoId);
        FFmpegProbeResult probeResult = ffprobe.probe(VideoEnv.ROOT_LOCATION.toString() + "/"
                        + videoId + "/" + videoFileName);
        FFmpegStream firstVideoStream = null;
        FFmpegStream firstAudioStream = null;
        // get the first video stream and audio stream
        FFmpegStream stream = null;
        for(int i = 0; i < probeResult.getStreams().size(); i++) {
            stream = probeResult.getStreams().get(i);
            if(firstVideoStream == null && stream.codec_type == FFmpegStream.CodecType.VIDEO)
                firstVideoStream = stream;
            if(firstAudioStream == null && stream.codec_type == FFmpegStream.CodecType.AUDIO)
                firstAudioStream = stream;

            if(firstVideoStream != null && i >= 1) {
                break;
            }
        }

        VideoFileMetadata videoFileMetadata = new VideoFileMetadata();
        // video
        videoFileMetadata.setId(videoId);
        videoFileMetadata.setPathName(VideoEnv.ROOT_LOCATION.toString() + "/" + videoId + "/" + videoFileName);
        videoFileMetadata.setWidth(firstVideoStream.width);
        videoFileMetadata.setHeight(firstVideoStream.height);
        videoFileMetadata.setFps(firstVideoStream.r_frame_rate.floatValue());
        videoFileMetadata.setVideoBitrate(firstVideoStream.bit_rate);
        videoFileMetadata.setVideoCodec(firstVideoStream.codec_name);

        // audio
        if(firstAudioStream != null) {
            videoFileMetadata.setAudioCodec(firstAudioStream.codec_name);
            videoFileMetadata.setAudioBitrate(firstAudioStream.bit_rate);
        }
        videoFileMetadata.setFormat(probeResult.getFormat().format_name);

        return videoFileMetadata;
    }
    public Boolean transcodeVideo(VideoFileMetadata v) throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder();
        for(Integer Quality : VideoEnv.VIDEO_QUALITY) {
            if(v.getWidth() > Quality) {
                int newHeight = Quality;
                int newWidth = (v.getWidth() * newHeight) / v.getHeight();
                newWidth = (newWidth % 2 != 0)? newWidth + 1 : newWidth;
                long newVideoBitrate = newWidth * newHeight * v.getVideoBitrate() / ((long) v.getWidth() * v.getHeight());
                long newAudioBitrate = (v.getAudioBitrate() > VideoEnv.AUDIO_BITRATE)? VideoEnv.AUDIO_BITRATE : v.getAudioBitrate();

                if(newWidth % 2 != 0) newWidth ++;

                // create dir for each quality
                String qualityDir = VideoEnv.ROOT_LOCATION.toString() + "/" + v.getId() + "/" + Quality.toString();

                Files.createDirectories(Paths.get(qualityDir));
                builder.setInput(v.getPathName())
                        .addOutput(qualityDir + "/" + Quality.toString() +
                                FileSystemStorageService.getFileExtensionFromOriginalName(v.getPathName()))
                        // video
                        .addExtraArgs("-map", "0:v:0")
                        .setVideoCodec("libx264")
                        .setVideoBitRate(newVideoBitrate)
                        .setVideoResolution(newWidth, newHeight)
                        // audio
                        .addExtraArgs("-map", "0:a:0")
                        .setAudioCodec("aac")
                        .setAudioBitRate(newAudioBitrate)

                        .setFormat("dash")
                        .done();
                ffmpeg.run(builder);
            }
        }
        return true;
    }
    public Boolean createManifestFile() {
        return true;
    }
    public String getVideoFileName(String videoId) {
        File dir =  new File(VideoEnv.ROOT_LOCATION.toString() + "/" + videoId);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("File is not a directory");
        }

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            String name = file.getName();
            if (name.startsWith(videoId)) {
                return name;  // Return the full file name with the extension
            }
        }
        return null;
    }
}
