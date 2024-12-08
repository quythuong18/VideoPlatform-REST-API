package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.File.storage.FileSystemStorageService;
import com.qt.VideoPlatformAPI.Config.VideoEnv;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FFmpegService {
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private final VideoService videoService;

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

    public VideoFileMetadata transcodeVideo(VideoFileMetadata v) throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder();
        for(Integer quality : VideoEnv.VIDEO_QUALITY) {
            if(v.getHeight() >= quality) {
                v.getQualities().add(quality);
                int newHeight = quality;
                int newWidth = (v.getWidth() * newHeight) / v.getHeight();
                newWidth = (newWidth % 2 != 0)? newWidth + 1 : newWidth;
                long newVideoBitrate = newWidth * newHeight * v.getVideoBitrate() / ((long) v.getWidth() * v.getHeight());
                long newAudioBitrate = 0;
                // check if Audio exists on that video
                if(v.getAudioBitrate() != null)
                    newAudioBitrate = (v.getAudioBitrate() > VideoEnv.AUDIO_BITRATE)? VideoEnv.AUDIO_BITRATE : v.getAudioBitrate();

                // create dir for each quality
                String VideoDir = VideoEnv.ROOT_LOCATION.toString() + "/" + v.getId();

                // get the file extension
                v.setFileExtension(FileSystemStorageService.getFileExtensionFromOriginalName(v.getPathName()));

                try {
                    builder
                            .addExtraArgs("-hwaccel", "cuda")
                            . setInput(v.getPathName())
                            .addOutput(VideoDir + "/" + quality.toString() + v.getFileExtension())
                            // video
                            .addExtraArgs("-map", "0:v:0")
                            .setVideoCodec("h264_nvenc")
                            .setVideoBitRate(newVideoBitrate)
                            .setVideoResolution(newWidth, newHeight)
                            // audio
                            .addExtraArgs("-map", "0:a:0")
                            .setAudioCodec("aac")
                            .setAudioBitRate(newAudioBitrate)

                            .addExtraArgs("-init_seg_name", quality.toString() + "-init-stream-$RepresentationID$.m4s")
                            .addExtraArgs("-media_seg_name", quality.toString() + "-chunk-stream-$RepresentationID$-$Number%05d$.m4s")
                            .setFormat("dash")
                            .done();
                    ffmpeg.run(builder);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return v;
    }

    // because of some problems we dont use bramp/ffmpeg-cli-wrapper here
    // instead we use process builder directly
    public VideoFileMetadata createManifestFile(VideoFileMetadata v) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(ffmpeg.getPath());

        String videoDir = VideoEnv.ROOT_LOCATION + "/" + v.getId();
        for(Integer quality : v.getQualities()) {
            command.add("-i");
            command.add(quality.toString() + v.getFileExtension());
        }
        StringBuilder stream = new StringBuilder("0");
        for(int i = 0; i < v.getQualities().size(); i++) {
            command.add("-map");
            command.add(String.valueOf(String.valueOf(i)));
            stream.append(",").append(Integer.toString(i));
        }
        command.add("-f");
        command.add("dash");
        command.add("output.mpd");
        // run the command here
        try {
            System.out.println("Executing command:");
            System.out.println(String.join(" ", command));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            pb.directory(new File(videoDir));
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return v;
    }
    // this function will return file extension within ".", e.g: ".mp4"
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

    // this function will
    public void createVideoThumbnailFrame(String videoId) throws IOException {
        Video v = videoService.getVideoById(videoId);
        if(!v.getIsUploaded())
            throw new IllegalArgumentException("Video has been not uploaded yet");

        FFmpegBuilder builder = new FFmpegBuilder();
        try {
            builder.setInput(VideoEnv.ROOT_LOCATION.toString() + "/" + videoId + "/" + getVideoFileName(v.getId()))
                    .addOutput(VideoEnv.ROOT_LOCATION.toString() + "/" + videoId + "/" + "thumbnail.jpg")
                    .addExtraArgs("-ss", "00:00:01")
                    .addExtraArgs("-vframes", "1")
                    .done();
            ffmpeg.run(builder);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
