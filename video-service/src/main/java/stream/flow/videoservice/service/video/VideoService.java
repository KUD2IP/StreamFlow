package stream.flow.videoservice.service.video;

import stream.flow.videoservice.model.dto.request.VideoCreateFrameRequest;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoFrameResponse;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.model.enums.Status;

import java.util.UUID;

/**
 * Основной сервис для управления видео (Domain Service)
 */
public interface VideoService {

    VideoFrameResponse createVideoFrame(VideoCreateFrameRequest request);

    /**
     * Создает новое видео (только метаданные, без файла)
     */
    VideoResponse createVideo(VideoCreateRequest request, String userId, UUID videoId);

    /**
     * Обновляет статус видео (используется при обработке)
     */
    void updateVideoStatus(UUID videoId, Status newStatus);
}




