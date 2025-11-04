package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.exception.user.UserNotFoundException;
import stream.flow.videoservice.exception.video.VideoNotFoundException;
import stream.flow.videoservice.mapper.VideoMapper;
import stream.flow.videoservice.model.dto.request.VideoCreateFrameRequest;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoFrameResponse;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.entity.VideoAnalytics;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.repository.UsersRepository;
import stream.flow.videoservice.repository.VideoAnalyticsRepository;
import stream.flow.videoservice.repository.VideoRepository;
import stream.flow.videoservice.service.video.VideoService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final UsersRepository usersRepository;
    private final VideoAnalyticsRepository videoAnalyticsRepository;
    private final VideoMapper videoMapper;
    private final stream.flow.videoservice.service.user.UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoFrameResponse createVideoFrame(VideoCreateFrameRequest request) {
        log.info("Create video frame request: {}", request);

        userService.syncUser();

        Users user = usersRepository.findByKeycloakId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        Video video = videoMapper.toEntity(request);
        video.setUser(user);

        log.info("Create video frame successful: {}", video);
        return videoMapper.toFrameResponse(videoRepository.save(video));
    }

    @Override
    @Transactional
    public VideoResponse createVideo(VideoCreateRequest request, String userId,  UUID videoId) {
        log.info("Creating video for user: {}", userId);

        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));

        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setVisibility(request.getVisibility());

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

        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));

        video.setStatus(newStatus);
        videoRepository.save(video);

        log.info("Video status updated: {}", videoId);
    }
}


