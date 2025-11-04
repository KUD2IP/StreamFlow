package stream.flow.videoservice.service.video.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stream.flow.videoservice.service.video.processor.TempFileManager;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

@Slf4j
@Service
public class TempFileManagerImpl implements TempFileManager {

    @Value("${video.temp-dir}")
    private String tempDir;

    @Override
    public String buildTempFilePath(UUID videoId, String quality) {
        return String.format("%s/%s/%s.mp4", tempDir, videoId, quality.toLowerCase());
    }

    @Override
    public void deleteTempDirectory(UUID videoId) {
        try {
            Path videoDir = Paths.get(tempDir, videoId.toString());

            if (!Files.exists(videoDir)) {
                log.warn("Temp directory does not exist: {}", videoDir);
                return;
            }

            // Удаляем всю директорию со всем содержимым
            Files.walkFileTree(videoDir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    log.debug("Deleted file: {}", file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    log.debug("Deleted directory: {}", dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("Temp directory deleted successfully: {}", videoDir);

        } catch (IOException e) {
            log.error("Failed to delete temp directory for videoId: {}", videoId, e);
            // Не пробрасываем исключение, чтобы не прервать основной процесс
        }
    }
}

