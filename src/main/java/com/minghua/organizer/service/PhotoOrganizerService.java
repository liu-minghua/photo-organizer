package com.minghua.organizer.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class PhotoOrganizerService {

    private static final Logger log = LoggerFactory.getLogger(PhotoOrganizerService.class);

    private final ExifService exifService;

    public PhotoOrganizerService(ExifService exifService) {
        this.exifService = exifService;
    }

    public Path organize(Path file, Path baseTargetDir) {
        LocalDateTime date = exifService.extractDate(file);

        String year = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());

        Path targetDir = baseTargetDir.resolve(year).resolve(month);


        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
        } catch (Exception e) {
            log.error("Unable to create directory {}: {}", targetDir, e.getMessage());
            throw new RuntimeException("Unable to create directory: " + targetDir);
        }

        Path targetFile = targetDir.resolve(file.getFileName());

        try {
            Path moved = Files.move(
                    file,
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
            log.debug("Moved file {} → {}", file, moved);
            return moved;
        } catch (Exception e) {
            log.error("Failed to move file {} → {}: {}", file, targetFile, e.getMessage());
            throw new RuntimeException("Failed to move file: " + file + " → " + targetFile);
        }
    }
}