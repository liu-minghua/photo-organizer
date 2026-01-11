package com.minghua.organizer.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class ExifService {

    public LocalDateTime extractDate(Path file) {
        LocalDateTime exifDate = readExifDate(file);
        if (exifDate != null) {
            return exifDate;
        }
        return fallbackFileTimestamp(file);
    }

    private LocalDateTime readExifDate(Path file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());
            ExifSubIFDDirectory dir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (dir != null && dir.getDateOriginal() != null) {
                Instant instant = dir.getDateOriginal().toInstant();
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }

        } catch (Exception ignored) {
        }
        return null;
    }

    private LocalDateTime fallbackFileTimestamp(Path file) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            Instant instant = attrs.creationTime().toInstant();
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        } catch (Exception e) {
            throw new RuntimeException("Unable to read timestamp for: " + file, e);
        }
    }
}