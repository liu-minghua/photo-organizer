package com.minghua.organizer.service;

import com.minghua.organizer.record.DuplicateCheckResult;
import org.junit.jupiter.api.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateDetectorServiceTest {

    private DuplicateDetectorService service;

    @BeforeEach
    void setup() {
        service = new DuplicateDetectorService();
    }

    @Test
    void testDetectsDuplicateFiles() throws Exception {
        Path temp = Files.createTempDirectory("dup");

        Path f1 = Files.writeString(temp.resolve("a.txt"), "hello");
        Path f2 = Files.writeString(temp.resolve("b.txt"), "hello");

        DuplicateCheckResult r1 = service.check(f1);
        DuplicateCheckResult r2 = service.check(f2);

        assertFalse(r1.isDuplicate());
        assertTrue(r2.isDuplicate());
    }
}