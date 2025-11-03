package stream.flow.videoservice.service.video;

import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.entity.VideoMetadata;

import java.util.UUID;

/**
 * Сервис для асинхронной обработки видео
 */
public interface VideoProcessingService {

    /**
     * Обрабатывает видео асинхронно
     * Извлекает метаданные, генерирует превью (если нужно), обновляет статус
     */
    void processVideo(UUID videoId, MultipartFile videoFile);

    /**
     * Извлекает метаданные из видео файла
     */
    VideoMetadata extractMetadata(MultipartFile videoFile, UUID videoId);

    /**
     * Генерирует превью для видео (если не загружено вручную)
     */
    void generateThumbnailIfNeeded(UUID videoId);
}




