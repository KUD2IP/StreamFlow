package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.dto.request.VideoCreateFrameRequest;
import stream.flow.videoservice.model.dto.response.VideoFrameResponse;
import stream.flow.videoservice.model.dto.response.VideoUploadResponse;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.model.enums.Visibility;
import stream.flow.videoservice.service.video.AsyncProcessVideoService;
import stream.flow.videoservice.service.video.VideoUploadService;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.service.file.FileService;
import stream.flow.videoservice.service.validation.VideoValidationService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoUploadServiceImpl implements VideoUploadService {

    private final VideoService videoService;
    private final VideoValidationService validationService;
    private final FileService fileService;
    private final AsyncProcessVideoService asyncProcessVideoService;

    @Value("${minio.bucket.videos:streamflow-videos}")
    private String videoBucket;

    @Value("${minio.bucket.thumbnails:streamflow-thumbnails}")
    private String thumbnailBucket;

    @Value("${video.temp-dir}")
    private String tempDir;

    @Override
    public VideoUploadResponse uploadVideo(MultipartFile videoFile, String userId) throws IOException {
        log.info("Starting video upload, user: {}", userId);

        validationService.validateVideoFile(videoFile);

        VideoFrameResponse video = videoService.createVideoFrame(VideoCreateFrameRequest.builder()
                .title(videoFile.getOriginalFilename())
                .visibility(Visibility.PUBLIC)
                .status(Status.UPLOADING)
                .userId(userId)
                .build());

        String pathOriginal = saveOriginal(videoFile, video.getVideoId());

        asyncProcessVideoService.processingAsync(pathOriginal, video.getVideoId());

        return VideoUploadResponse.builder()
                .videoId(video.getVideoId())
                .status(Status.UPLOADING)
                .message("Video upload initiated successfully")
                .build();
    }

    @Override
    public VideoUploadResponse uploadThumbnail(UUID videoId, MultipartFile thumbnailFile, String userId) {
        log.info("Starting thumbnail upload for videoId: {}, user: {}", videoId, userId);

        return null;
    }

    private String saveOriginal(MultipartFile videoFile, UUID videoId) throws IOException {
        // Создаем директорию для видео
        Path videoDir = Paths.get(tempDir, videoId.toString());
        Files.createDirectories(videoDir);

        String extension = fileService.getFileExtension(videoFile.getOriginalFilename());
        
        // Путь к файлу оригинала
        Path originalPath = videoDir.resolve("original." + extension);
        
        // Сохраняем файл
        try (OutputStream outputStream = Files.newOutputStream(originalPath);
             InputStream inputStream = videoFile.getInputStream()) {
            
            inputStream.transferTo(outputStream);
        }
        
        log.info("Original video saved to: {}", originalPath);

        return originalPath.toString();
    }
}


