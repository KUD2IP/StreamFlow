package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.exception.user.UserNotFoundException;
import stream.flow.videoservice.mapper.VideoMapper;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.entity.VideoAnalytics;
import stream.flow.videoservice.model.entity.VideoTags;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.repository.UsersRepository;
import stream.flow.videoservice.repository.VideoAnalyticsRepository;
import stream.flow.videoservice.repository.VideoRepository;
import stream.flow.videoservice.repository.VideoTagsRepository;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.service.validation.VideoValidationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final UsersRepository usersRepository;
    private final VideoTagsRepository videoTagsRepository;
    private final VideoAnalyticsRepository videoAnalyticsRepository;
    private final VideoMapper videoMapper;
    private final VideoValidationService validationService;

    @Override
    @Transactional
    public VideoResponse createVideo(VideoCreateRequest request, String userId) {
        log.info("Creating video for user: {}", userId);

        // Получаем пользователя
        Users user = usersRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Создаем Video entity
        Video video = videoMapper.toEntity(request);
        video.setUser(user);

        // Устанавливаем теги если есть
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<VideoTags> tags = videoTagsRepository.findAllById(request.getTagIds());
            video.setTags(tags);
        }

        // Сохраняем видео
        Video savedVideo = videoRepository.save(video);

        // Создаем analytics для видео
        VideoAnalytics analytics = VideoAnalytics.builder()
                .video(savedVideo)
                .viewsCount(0L)
                .likesCount(0L)
                .commentCount(0L)
                .build();
        videoAnalyticsRepository.save(analytics);

        log.info("Video created successfully with ID: {}", savedVideo.getId());
        return videoMapper.toResponse(savedVideo);
    }

    @Override
    @Transactional
    public void updateVideoStatus(UUID videoId, Status newStatus) {
        log.info("Updating video status: {} to {}", videoId, newStatus);

        Video video = validationService.validateVideoExists(videoId);
        video.setStatus(newStatus);
        videoRepository.save(video);

        log.info("Video status updated: {}", videoId);
    }

    @Override
    @Transactional
    public void updateVideoUrls(UUID videoId, String videoUrl, String previewUrl) {
        log.info("Updating video URLs for: {}", videoId);

        Video video = validationService.validateVideoExists(videoId);
        
        if (videoUrl != null) {
            video.setVideoUrl(videoUrl);
        }
        if (previewUrl != null) {
            video.setPreviewUrl(previewUrl);
        }

        videoRepository.save(video);
        log.info("Video URLs updated: {}", videoId);
    }

    @Override
    public boolean videoExists(UUID videoId) {
        return videoRepository.existsById(videoId);
    }

    @Override
    @Transactional
    public void updateVideoFilename(UUID videoId, String filename) {
        log.info("Updating video filename for: {}", videoId);

        Video video = validationService.validateVideoExists(videoId);
        video.setFilename(filename);
        videoRepository.save(video);

        log.info("Video filename updated: {}", videoId);
    }
}


