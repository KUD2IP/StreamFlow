package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.entity.VideoMetadata;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.repository.VideoMetadataRepository;
import stream.flow.videoservice.service.video.VideoProcessingService;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.service.validation.VideoValidationService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessingServiceImpl implements VideoProcessingService {

    private final VideoService videoService;
    private final VideoMetadataRepository metadataRepository;
    private final VideoValidationService validationService;

    @Override
    @Async
    @Transactional
    public void processVideo(UUID videoId, MultipartFile videoFile) {
        log.info("Starting async video processing for: {}", videoId);

        try {
            // 1. Обновляем статус на PROCESSING
            videoService.updateVideoStatus(videoId, Status.PROCESSING);

            // 2. Извлекаем метаданные
            extractMetadata(videoFile, videoId);
            
            // 3. Генерируем превью если нужно
            generateThumbnailIfNeeded(videoId);

            // 4. Обновляем статус на READY
            videoService.updateVideoStatus(videoId, Status.READY);

            log.info("Video processing completed successfully: {}", videoId);

        } catch (Exception e) {
            log.error("Video processing failed for {}: {}", videoId, e.getMessage(), e);
            videoService.updateVideoStatus(videoId, Status.FAILED);
        }
    }

    @Override
    @Transactional
    public VideoMetadata extractMetadata(MultipartFile videoFile, UUID videoId) {
        log.info("Extracting metadata for video: {}", videoId);

        try {
            Video video = validationService.validateVideoExists(videoId);

            // TODO: Здесь должна быть реальная логика извлечения метаданных
            // Можно использовать библиотеки типа Xuggler, JavaCV, или вызывать FFmpeg
            // Пока создаем заглушку с базовыми данными

            VideoMetadata metadata = VideoMetadata.builder()
                    .video(video)
                    .filesize(videoFile.getSize())
                    .duration(0) // TODO: извлечь реальную длительность
                    .bitrate(0L) // TODO: извлечь реальный bitrate
                    .resolution("unknown") // TODO: извлечь реальное разрешение
                    .build();

            VideoMetadata savedMetadata = metadataRepository.save(metadata);
            log.info("Metadata extracted and saved for video: {}", videoId);

            return savedMetadata;

        } catch (Exception e) {
            log.error("Failed to extract metadata: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract video metadata", e);
        }
    }

    @Override
    @Transactional
    public void generateThumbnailIfNeeded(UUID videoId) {
        log.info("Checking if thumbnail generation needed for: {}", videoId);

        Video video = validationService.validateVideoExists(videoId);

        // Если превью уже загружено, пропускаем генерацию
        if (video.getPreviewUrl() != null && !video.getPreviewUrl().isEmpty()) {
            log.info("Thumbnail already exists, skipping generation: {}", videoId);
            return;
        }

        // TODO: Здесь должна быть логика генерации превью из видео
        // Можно использовать FFmpeg для извлечения кадра из видео
        // Пока просто логируем

        log.info("TODO: Generate thumbnail for video: {}", videoId);
    }
}

