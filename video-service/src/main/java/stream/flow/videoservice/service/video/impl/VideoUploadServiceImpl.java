package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.dto.response.VideoUploadResponse;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.service.video.VideoProcessingService;
import stream.flow.videoservice.service.video.VideoUploadService;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.service.storage.StorageService;
import stream.flow.videoservice.service.validation.FileUtilService;
import stream.flow.videoservice.service.validation.VideoValidationService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoUploadServiceImpl implements VideoUploadService {

    private final StorageService storageService;
    private final VideoService videoService;
    private final VideoValidationService validationService;
    private final FileUtilService fileUtilService;
    private final VideoProcessingService processingService;

    @Value("${minio.bucket.videos:streamflow-videos}")
    private String videoBucket;

    @Value("${minio.bucket.thumbnails:streamflow-thumbnails}")
    private String thumbnailBucket;

    @Override
    public VideoUploadResponse uploadVideo(UUID videoId, MultipartFile videoFile, String userId) {
        log.info("Starting video upload for videoId: {}, user: {}", videoId, userId);

        try {
            // 1. Проверяем права доступа
            validationService.validateUserOwnership(videoId, userId);

            // 2. Валидация файла
            validationService.validateVideoFile(videoFile);

            // 3. Генерируем уникальное имя файла
            String fileName = fileUtilService.generateUniqueFileName(videoFile.getOriginalFilename());

            // 4. Загружаем файл в MinIO
            String videoUrl = storageService.uploadFile(videoFile, videoBucket, fileName);

            // 5. Обновляем URL в БД и сохраняем имя файла
            videoService.updateVideoUrls(videoId, videoUrl, null);
            videoService.updateVideoFilename(videoId, fileName);

            // 6. Запускаем асинхронную обработку видео
            processingService.processVideo(videoId, videoFile);

            log.info("Video uploaded successfully: {}", videoId);

            return VideoUploadResponse.builder()
                    .videoId(videoId)
                    .status(Status.PROCESSING)
                    .message("Video uploaded successfully and is being processed")
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload video: {}", e.getMessage(), e);
            videoService.updateVideoStatus(videoId, Status.FAILED);
            
            throw e;
        }
    }

    @Override
    public VideoUploadResponse uploadThumbnail(UUID videoId, MultipartFile thumbnailFile, String userId) {
        log.info("Starting thumbnail upload for videoId: {}, user: {}", videoId, userId);

        try {
            // 1. Проверка прав доступа
            validationService.validateUserOwnership(videoId, userId);

            // 2. Валидация файла
            validationService.validateThumbnailFile(thumbnailFile);

            // 3. Генерируем уникальное имя файла
            String fileName = fileUtilService.generateUniqueFileName(thumbnailFile.getOriginalFilename());

            // 4. Загружаем файл в MinIO
            String thumbnailUrl = storageService.uploadFile(thumbnailFile, thumbnailBucket, fileName);

            // 5. Обновляем URL в БД
            videoService.updateVideoUrls(videoId, null, thumbnailUrl);

            Video video = validationService.validateVideoExists(videoId);

            log.info("Thumbnail uploaded successfully: {}", videoId);

            return VideoUploadResponse.builder()
                    .videoId(videoId)
                    .status(video.getStatus())
                    .message("Thumbnail uploaded successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload thumbnail: {}", e.getMessage(), e);
            throw e;
        }
    }
}


