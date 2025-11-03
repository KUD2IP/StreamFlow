package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.exception.user.UserNotFoundException;
import stream.flow.videoservice.mapper.VideoMapper;
import stream.flow.videoservice.model.dto.request.VideoCreateFrameRequest;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoFrameResponse;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.model.entity.Tag;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.entity.VideoAnalytics;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.repository.TagRepository;
import stream.flow.videoservice.repository.UsersRepository;
import stream.flow.videoservice.repository.VideoAnalyticsRepository;
import stream.flow.videoservice.repository.VideoRepository;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.service.validation.VideoValidationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final UsersRepository usersRepository;
    private final TagRepository tagRepository;
    private final VideoAnalyticsRepository videoAnalyticsRepository;
    private final VideoMapper videoMapper;
    private final VideoValidationService validationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoFrameResponse createVideoFrame(VideoCreateFrameRequest request) {
        log.info("Create video frame request: {}", request);

        Users user = usersRepository.findByKeycloakId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        Video video = videoMapper.toEntity(request);
        video.setUser(user);

        log.info("Create video frame successful: {}", video);
        return videoMapper.toFrameResponse(videoRepository.save(video));
    }

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
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            video.setTags(tags);
        }

        // Сохраняем видео
        Video savedVideo = videoRepository.save(video);

        // Создаем analytics для видео
        VideoAnalytics analytics = VideoAnalytics.builder()
                .video(savedVideo)
                .viewsCount(0L)
                .likesCount(0L)
                .commentsCount(0L)
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
}


