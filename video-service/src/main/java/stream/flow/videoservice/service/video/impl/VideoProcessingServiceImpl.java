package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.entity.VideoQuality;
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
    }

    @Override
    @Transactional
    public VideoQuality extractMetadata(MultipartFile videoFile, UUID videoId) {
        log.info("Extracting metadata for video: {}", videoId);
        return null;
    }

    @Override
    @Transactional
    public void generateThumbnailIfNeeded(UUID videoId) {
        log.info("Checking if thumbnail generation needed for: {}", videoId);
    }
}

