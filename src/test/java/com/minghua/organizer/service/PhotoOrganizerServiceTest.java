package com.minghua.organizer.service;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhotoOrganizerServiceTest {

    private ExifService exifService;
    private PhotoOrganizerService organizer;

    @BeforeEach
    void setup() {
        exifService = mock(ExifService.class);
        organizer = new PhotoOrganizerService(exifService);
    }

    @Test
    void testOrganizeMovesFileIntoYearMonth() throws Exception {
        Path tempSource = Files.createTempDirectory("src");
        Path tempTarget = Files.createTempDirectory("tgt");

        Path file = Files.createFile(tempSource.resolve("photo.jpg"));

        when(exifService.extractDate(file))
                .thenReturn(java.time.LocalDateTime.of(2024, 1, 15, 10, 0));

        Path result = organizer.organize(file, tempTarget);

        assertTrue(Files.exists(result));
        assertTrue(result.toString().contains("2024"));
        assertTrue(result.toString().contains("01"));
    }
}