package com.minghua.organizer.service;

import com.minghua.organizer.record.DuplicateCheckResult;
import com.minghua.organizer.record.MigrationSummary;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MigrationServiceTest {

    private DuplicateDetectorService duplicateDetector;
    private PhotoOrganizerService organizer;
    private MigrationService migrationService;
    private ExifService exifService;

    @BeforeEach
    void setup() {
        duplicateDetector = mock(DuplicateDetectorService.class);
        organizer = mock(PhotoOrganizerService.class);
        exifService = mock(ExifService.class);
        migrationService = new MigrationService(organizer, duplicateDetector, exifService);
    }

    @Test
    void testMigrationProcessesFilesCorrectly() throws Exception {
        Path tempSource = Files.createTempDirectory("src");
        Path tempTarget = Files.createTempDirectory("tgt");

        Path file1 = Files.createFile(tempSource.resolve("a.jpg"));
        Path file2 = Files.createFile(tempSource.resolve("b.jpg"));

        when(duplicateDetector.check(any())).thenReturn(
                new DuplicateCheckResult(false, false),
                new DuplicateCheckResult(true, false)
        );

        when(organizer.organize(eq(file1), eq(tempTarget)))
                .thenReturn(tempTarget.resolve("2024/01/a.jpg"));

        AtomicInteger moved = new AtomicInteger();
        AtomicInteger duplicates = new AtomicInteger();

        MigrationSummary summary = migrationService.run(
                tempSource,
                tempTarget,
                new MigrationListener() {
                    @Override
                    public void onMoved(Path s, Path t) {
                        moved.incrementAndGet();
                    }

                    @Override
                    public void onInfo(String message) {

                    }

                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onComplete(MigrationSummary summary) {

                    }

                    @Override
                    public void onDuplicate(Path f) {
                        duplicates.incrementAndGet();
                    }
                }
        );

        assertEquals(1, moved.get());
        assertEquals(1, duplicates.get());
        assertEquals(2, summary.filesProcessed());
        assertEquals(1, summary.filesMoved());
        assertEquals(1, summary.duplicatesFound());
    }
    @Test
    void testEmptyFolderDeletionSummary() throws IOException {
        Path empty = Files.createTempDirectory("empty");

        MigrationService service = new MigrationService(organizer, duplicateDetector, exifService);

        TestListener listener = new TestListener();
        MigrationSummary summary = service.deleteEmptySourceAndBuildSummary(empty, listener);

        assertTrue(summary.deletedEmptySourceFolder());
        assertEquals(0, summary.totalFilesScanned());
        assertEquals(0, summary.filesCopied());
        assertEquals(0, summary.filesSkipped());
        assertEquals(0, summary.errors());
    }
}