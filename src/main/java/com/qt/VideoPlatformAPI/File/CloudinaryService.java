package com.qt.VideoPlatformAPI.File;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryService {
    private final Cloudinary cloudinary;
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
    public String uploadPhoto(MultipartFile file, String folder, String fileName) throws IOException {
        Map options = ObjectUtils.asMap(
                "folder", "videoplatform" + "/" + folder,
                "public_id", fileName
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("url");
    }
    public String updatePhoto(MultipartFile file, String folder, String fileName) throws IOException {
        Map options = ObjectUtils.asMap(
                "folder", "videoplatform" + "/" + folder,
                "public_id", fileName, // Use the same public_id to overwrite
                "overwrite", true      // Explicitly indicate overwrite (optional)
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("url");
    }
}
