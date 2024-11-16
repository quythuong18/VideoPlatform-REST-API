package com.qt.VideoPlatformAPI.File.storage;

import com.qt.VideoPlatformAPI.Exception.StorageException;
import com.qt.VideoPlatformAPI.Utils.VideoEnv;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileSystemStorageService {

    // the id will be the name of the dir and the video file itself
    public void store(MultipartFile file, String id) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("failed to store empty file.");
            }
            // create a sub dir to save file name
            Path videoSubDir = VideoEnv.ROOT_LOCATION.resolve(id);
            Files.createDirectories(videoSubDir);

            Path destinationFile = videoSubDir.resolve(Paths.get
                    (id + getFileExtensionFromOriginalName(file.getOriginalFilename()))).
                    normalize().toAbsolutePath(); // set the file name to id
            if (!destinationFile.getParent().equals(videoSubDir.toAbsolutePath())) {
                // this is a security check
                throw new StorageException("cannot store file outside current directory.");
            }
            try (InputStream inputstream = file.getInputStream()) {
                Files.copy(inputstream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("failed to store file.", e);
        }
    }

    public static String getFileExtensionFromOriginalName(String filename) {
        String extension = "";
        // Ensure the filename is not null and has an extension
        if (filename != null && filename.contains(".") && filename.lastIndexOf(".") != filename.length() - 1) {
            extension = filename.substring(filename.lastIndexOf("."));
        }

        return extension;
    }

}
