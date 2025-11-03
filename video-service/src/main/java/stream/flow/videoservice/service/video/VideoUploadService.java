package stream.flow.videoservice.service.video;

import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.dto.response.VideoUploadResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * Application Service для координации процесса загрузки видео
 */
public interface VideoUploadService {

    /**
     * Загружает видео файл для существующего видео
     * Координирует: валидацию → загрузку в MinIO → обновление БД → запуск обработки
     */
    VideoUploadResponse uploadVideo(MultipartFile videoFile, String userId) throws IOException;

    /**
     * Загружает превью (thumbnail) для видео
     */
    VideoUploadResponse uploadThumbnail(UUID videoId, MultipartFile thumbnailFile, String userId);
}




