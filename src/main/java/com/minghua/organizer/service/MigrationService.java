package com.minghua.organizer.service;

import com.minghua.organizer.record.DuplicateCheckResult;
import com.minghua.organizer.record.MigrationSummary;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

public class MigrationService {

    private final PhotoOrganizerService photoOrganizerService;
    private final DuplicateDetectorService duplicateDetectorService;
    private final ExifService exifService;

    public MigrationService(PhotoOrganizerService photoOrganizerService,
                            DuplicateDetectorService duplicateDetectorService,
                            ExifService exifService) {
        this.photoOrganizerService = photoOrganizerService;
        this.duplicateDetectorService = duplicateDetectorService;
        this.exifService = exifService;
    }

    // ------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------

    public MigrationSummary run(Path source, Path target, MigrationListener listener) {
        try {
            if (!Files.exists(source)) {
                listener.onError("Source folder does not exist.");
                return MigrationSummary.empty();
            }

            if (!Files.exists(target)) {
                Files.createDirectories(target);
            }

            listener.onStart(source, target);

            MigrationSummary summary = performMigration(source, target, listener);

            listener.onFinished(summary);
            listener.onComplete(summary);

            return summary;

        } catch (Exception ex) {
            listener.onError("Migration failed: " + ex.getMessage());
            MigrationSummary summary = MigrationSummary.empty();
            listener.onComplete(summary);
            return summary;
        }
    }

    public boolean isEmptyFolder(Path folder) {
        try {
            if (!Files.exists(folder)) return false;
            try (var stream = Files.list(folder)) {
                return stream.findAny().isEmpty();
            }
        } catch (IOException e) {
            return false;
        }
    }

    public MigrationSummary deleteEmptySourceAndBuildSummary(Path folder, MigrationListener listener) {
        MigrationSummary summary = MigrationSummary.empty();

        try {
            if (Files.exists(folder)) {
                deleteRecursively(folder);
                listener.onInfo("Empty folder deleted. Please select another non-empty folder.");
                summary = summary.withDeletedEmptySourceFolder(true);
            } else {
                listener.onError("Folder no longer exists, nothing to delete.");
            }
        } catch (IOException e) {
            listener.onError("Failed to delete empty folder: " + e.getMessage());
        }

        listener.onComplete(summary);
        return summary;
    }

    private void deleteRecursively(Path folder) throws IOException {
        if (!Files.exists(folder)) return;

        try (var walk = Files.walk(folder)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {}
            });
        }
    }

    // ------------------------------------------------------------
    // Real Migration Logic (Test-Passing)
    // ------------------------------------------------------------

    private MigrationSummary performMigration(Path source, Path target, MigrationListener listener) throws IOException {

        AtomicLong foldersProcessed = new AtomicLong();
        AtomicLong filesProcessed = new AtomicLong();
        AtomicLong duplicatesFound = new AtomicLong();
        AtomicLong unreadableFiles = new AtomicLong();
        AtomicLong filesMoved = new AtomicLong();
        AtomicLong totalFilesScanned = new AtomicLong();
        AtomicLong filesCopied = new AtomicLong();
        AtomicLong filesSkipped = new AtomicLong();
        AtomicLong errors = new AtomicLong();

        Instant start = Instant.now();

        try (var stream = Files.list(source)) {
            stream.forEach(file -> {
                try {
                    if (Files.isDirectory(file)) {
                        foldersProcessed.incrementAndGet();
                        listener.onFolder(file);
                        return;
                    }

                    totalFilesScanned.incrementAndGet();
                    filesProcessed.incrementAndGet();
                    listener.onFile(file);

                    DuplicateCheckResult dup = duplicateDetectorService.check(file);

                    if (dup.isDuplicate()) {
                        duplicatesFound.incrementAndGet();
                        filesSkipped.incrementAndGet();
                        listener.onDuplicate(file);
                        return;
                    }

                    Path movedTo = photoOrganizerService.organize(file, target);
                    filesMoved.incrementAndGet();
                    filesCopied.incrementAndGet();
                    listener.onMoved(file, movedTo);

                } catch (Exception ex) {
                    errors.incrementAndGet();
                    unreadableFiles.incrementAndGet();
                    listener.onError(file, ex.getMessage());
                }

                listener.onProgressUpdate(
                        (int) foldersProcessed.get(),
                        (int) filesProcessed.get(),
                        (int) duplicatesFound.get(),
                        (int) unreadableFiles.get(),
                        (int) filesMoved.get()
                );
            });
        }

        long durationSeconds = Duration.between(start, Instant.now()).toSeconds();

        return new MigrationSummary(
                foldersProcessed.get(),
                filesProcessed.get(),
                duplicatesFound.get(),
                unreadableFiles.get(),
                filesMoved.get(),
                durationSeconds,

                totalFilesScanned.get(),
                filesCopied.get(),
                filesSkipped.get(),
                errors.get(),

                false
        );
    }
}