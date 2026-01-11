package com.minghua.organizer.service;

import com.minghua.organizer.record.MigrationSummary;

import java.nio.file.Path;

public interface MigrationListener {

    default void onStart(Path source, Path target) {}

    default void onFolder(Path folder) {}

    default void onFile(Path file) {}

    default void onDuplicate(Path file) {}

    default void onUnreadable(Path file, String reason) {}

    default void onMoved(Path source, Path target) {}

    default void onError(Path file, String message) {}

    default void onProgressUpdate(
            int foldersProcessed,
            int filesProcessed,
            int duplicatesFound,
            int unreadableFiles,
            int filesMoved
    ) {}

    default void onFinished(MigrationSummary summary) {}

    void onInfo(String message);

    void onError(String message);

    void onComplete(MigrationSummary summary);

}