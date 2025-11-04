package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.exception.file.FileUploadException;
import stream.flow.videoservice.model.enums.Quality;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.service.video.VideoProcessingService;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.service.video.processor.TempFileManager;
import stream.flow.videoservice.service.video.processor.VideoQualityProcessor;

import java.util.UUID;

/**
 * Реализация сервиса обработки видео.
 * Оркестрирует процесс обработки видео в разные качества.
 * 
 * Ответственность:
 * - Координация процесса обработки всех качеств
 * - Управление статусом видео
 * - Обработка ошибок и очистка ресурсов
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessingServiceImpl implements VideoProcessingService {

    private final VideoQualityProcessor qualityProcessor;
    private final VideoService videoService;
    private final TempFileManager tempFileManager;

    @Override
    @Transactional
    public void processing(String pathOriginal, UUID videoId) {
        log.info("Starting video processing for videoId: {}", videoId);

        try {
            // Обновляем статус на PROCESSING
            videoService.updateVideoStatus(videoId, Status.PROCESSING);

            // Обрабатываем каждое качество
            processAllQualities(pathOriginal, videoId);

            // Обновляем статус на READY
            videoService.updateVideoStatus(videoId, Status.READY);
            log.info("Video processing completed successfully for videoId: {}", videoId);

        } catch (Exception e) {
            log.error("Video processing failed for videoId: {}", videoId, e);
            // Обновляем статус на FAILED
            videoService.updateVideoStatus(videoId, Status.FAILED);
            throw new FileUploadException("Failed to process video: " + e.getMessage(), e);
        } finally {
            // Всегда удаляем временные файлы
            tempFileManager.deleteTempDirectory(videoId);
        }
    }

    /**
     * Обрабатывает видео во все доступные качества
     */
    private void processAllQualities(String pathOriginal, UUID videoId) {
        log.debug("Processing all qualities for videoId: {}", videoId);

        for (Quality quality : Quality.values()) {
            try {
                String outputPath = tempFileManager.buildTempFilePath(videoId, quality.name());
                qualityProcessor.processQuality(pathOriginal, videoId, quality, outputPath);
                log.debug("Quality {} processed successfully", quality);

            } catch (Exception e) {
                log.error("Failed to process quality {} for videoId {}: {}", quality, videoId, e.getMessage(), e);
                // Продолжаем обработку других качеств даже если одно упало
                // Можно изменить логику, если нужно прерывать процесс при ошибке
            }
        }

        log.info("All qualities processed for videoId: {}", videoId);
    }
}
