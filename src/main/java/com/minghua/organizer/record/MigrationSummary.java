package com.minghua.organizer.record;

public record MigrationSummary(
        long foldersProcessed,
        long filesProcessed,
        long duplicatesFound,
        long unreadableFiles,
        long filesMoved,
        long durationSeconds,

        long totalFilesScanned,
        long filesCopied,
        long filesSkipped,
        long errors,

        boolean deletedEmptySourceFolder
) {

    public static MigrationSummary empty() {
        return new MigrationSummary(
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0,
                false
        );
    }

    public MigrationSummary withDeletedEmptySourceFolder(boolean deleted) {
        return new MigrationSummary(
                foldersProcessed,
                filesProcessed,
                duplicatesFound,
                unreadableFiles,
                filesMoved,
                durationSeconds,
                totalFilesScanned,
                filesCopied,
                filesSkipped,
                errors,
                deleted
        );
    }
}