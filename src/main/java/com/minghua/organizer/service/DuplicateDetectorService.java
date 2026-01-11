package com.minghua.organizer.service;

import com.minghua.organizer.record.DuplicateCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class DuplicateDetectorService {

    private static final Logger log = LoggerFactory.getLogger(DuplicateDetectorService.class);

    private final Set<String> seenHashes = ConcurrentHashMap.newKeySet();

    public DuplicateCheckResult check(Path file) {
        String hash = computeSha256(file);

        if (hash == null) {
            // unreadable or cloud-locked
            return new DuplicateCheckResult(false, true);
        }

        boolean duplicate = !seenHashes.add(hash);
        return new DuplicateCheckResult(duplicate, false);
    }

    private String computeSha256(Path file) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<String> future = executor.submit(() -> {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");

                try (InputStream is = Files.newInputStream(file)) {
                    byte[] buffer = new byte[65536];
                    int bytesRead;

                    while ((bytesRead = is.read(buffer)) != -1) {
                        digest.update(buffer, 0, bytesRead);
                    }
                }

                return bytesToHex(digest.digest());
            });

            // Timeout to avoid hanging on cloud-locked files
            return future.get(5, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.warn("Skipping unreadable or cloud-locked file {}: {}", file, e.getMessage());
            return null;

        } finally {
            executor.shutdownNow();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}