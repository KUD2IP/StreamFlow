package stream.flow.videoservice.service.video.processor.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stream.flow.videoservice.exception.file.FileUploadException;
import stream.flow.videoservice.model.dto.response.VideoInfoResponse;
import stream.flow.videoservice.model.enums.Quality;
import stream.flow.videoservice.service.ffmpeg.FfmpegService;
import stream.flow.videoservice.service.storage.StorageService;
import stream.flow.videoservice.service.video.VideoQualityService;
import stream.flow.videoservice.service.video.processor.VideoQualityProcessor;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoQualityProcessorImpl implements VideoQualityProcessor {

    private final FfmpegService ffmpegService;
    private final StorageService storageService;
    private final VideoQualityService videoQualityService;

    @Value("${minio.bucket.videos}")
    private String bucketName;

    @Override
    public ProcessingResult processQuality(String originalPath, UUID videoId, Quality quality, String outputPath) {
        log.info("Processing quality {} for video {}", quality, videoId);

        try {
            // Конвертация видео в нужное качество
            String localQualityPath = ffmpegService.convertQualityVideo(originalPath, outputPath, quality);
            log.debug("Video converted to quality {}: {}", quality, localQualityPath);

            // Извлечение метаданных из конвертированного видео
            VideoInfoResponse metadata = ffmpegService.extractVideoMetadata(localQualityPath);
            log.debug("Metadata extracted for quality {}: duration={}s, resolution={}", 
                    quality, metadata.getDuration(), metadata.getResolution());

            //Загрузка в хранилище
            String storagePath = storageService.uploadFile(localQualityPath, bucketName);
            log.debug("Video uploaded to storage: {}", storagePath);

            //Сохранение метаданных в БД
            videoQualityService.saveVideoMetadata(metadata, storagePath, videoId, quality);
            log.info("Quality {} processed successfully for video {}", quality, videoId);

            return new ProcessingResult(storagePath, metadata, localQualityPath);

        } catch (Exception e) {
            log.error("Failed to process quality {} for video {}: {}", quality, videoId, e.getMessage(), e);
            throw new FileUploadException("Failed to process quality " + quality + ": " + e.getMessage(), e);
        }
    }
}

