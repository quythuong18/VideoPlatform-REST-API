package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.File.storage.FileSystemStorageService;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import com.qt.VideoPlatformAPI.Utils.VideoConstants;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class UploadFileController {
    private final Logger logger;
    private final FileSystemStorageService fileSystemStorageService;
    private final VideoService videoService;
    private final VideoFileProcessingService videoFileProcessingService;

    @PostMapping("/video/{id}")
    public ResponseEntity<APIResponse> handleVideoUpload(@RequestBody MultipartFile file, @PathVariable(name = "id") String id) throws FileUploadException, IOException {

        Video video = videoService.getVideoById(id);
        if(video.getIsUploaded())
            throw new FileUploadException("The video has been uploaded");

        if(file.isEmpty())
            throw new IllegalArgumentException("Please upload a video file");

        String contentType = file.getContentType();
        if(contentType == null || !VideoConstants.VIDEO_MIME_TYPES.contains(contentType)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new APIResponse(false, "Invalid video file type", HttpStatus.UNSUPPORTED_MEDIA_TYPE));
        }

        fileSystemStorageService.store(file, id); // save video file upload
        videoService.updateVideoUploadedStatus(id); // set video metadata status
        logger.info("Video file has been uploaded");
        logger.info("Video file is processing");
        videoFileProcessingService.processVideoAsync(id); // video processing Completable

        return ResponseEntity.ok(new APIResponse(true, "Video uploaded successfully and being processed", HttpStatus.OK));
    }

}
