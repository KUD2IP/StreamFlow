package stream.flow.videoservice.service.video;

import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.model.enums.Status;

import java.util.UUID;

/**
 * Основной сервис для управления видео (Domain Service)
 */
public interface VideoService {

    /**
     * Создает новое видео (только метаданные, без файла)
     */
    VideoResponse createVideo(VideoCreateRequest request, String userId);

    /**
     * Обновляет статус видео (используется при обработке)
     */
    void updateVideoStatus(UUID videoId, Status newStatus);

    /**
     * Обновляет URL видео и превью после загрузки файлов
     */
    void updateVideoUrls(UUID videoId, String videoUrl, String previewUrl);

    /**
     * Проверяет существование видео
     */
    boolean videoExists(UUID videoId);

    /**
     * Обновляет имя файла видео
     */
    void updateVideoFilename(UUID videoId, String filename);
}




