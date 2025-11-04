package stream.flow.videoservice.service.video.processor;

import java.util.UUID;

/**
 * Управление временными файлами видео
 */
public interface TempFileManager {

    /**
     * Создает путь для временного файла определенного качества
     * 
     * @param videoId ID видео
     * @param quality качество
     * @return путь к временному файлу
     */
    String buildTempFilePath(UUID videoId, String quality);

    /**
     * Удаляет временную директорию для видео
     * 
     * @param videoId ID видео
     */
    void deleteTempDirectory(UUID videoId);
}

