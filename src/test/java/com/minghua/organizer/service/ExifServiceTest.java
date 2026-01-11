package com.minghua.organizer.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ExifServiceTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withBean(ExifService.class);

    @Test
    void fallbackTimestampWorksWhenNoExif() throws Exception {
        contextRunner.run(context -> {
            ExifService service = context.getBean(ExifService.class);

            // Create a temporary file to simulate a photo with no EXIF
            Path tempFile = Files.createTempFile("photo", ".jpg");

            LocalDateTime date = service.extractDate(tempFile);

            assertThat(date).isNotNull();
            assertThat(date.getYear()).isGreaterThan(2000);

            Files.deleteIfExists(tempFile);
        });
    }
}