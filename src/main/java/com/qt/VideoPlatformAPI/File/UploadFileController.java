package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.File.storage.FileSystemStorageService;
import com.qt.VideoPlatformAPI.Responses.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class UploadFileController {
    private final FileSystemStorageService fileSystemStorageService;
    private final List<String> VIDEO_MIME_TYPES = Arrays.asList(
            "video/mp4", "video/mkv", "video/avi", "video/mpeg", "video/webm"
    );
    @PostMapping("/video")
    public ResponseEntity<APIResponse>  handleVideoUpload(@RequestBody MultipartFile file) {
        if(file.isEmpty())
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse(false, "Please upload a video file", HttpStatus.BAD_REQUEST));

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);
        if(contentType == null || !VIDEO_MIME_TYPES.contains(contentType)) {
            ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new APIResponse(false, "Invalid video file type", HttpStatus.UNSUPPORTED_MEDIA_TYPE));
        }

        fileSystemStorageService.store(file);

        return ResponseEntity.ok(new APIResponse(true, "Video uploaded successfully", HttpStatus.OK));
    }

}