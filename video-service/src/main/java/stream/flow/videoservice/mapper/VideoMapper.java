package stream.flow.videoservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.request.VideoCreateFrameRequest;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoFrameResponse;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.enums.Status;

@Component
@RequiredArgsConstructor
public class VideoMapper {

    private final UserMapper userMapper;
    private final TagMapper tagMapper;
    private final VideoMetadataMapper videoMetadataMapper;
    private final VideoAnalyticsMapper videoAnalyticsMapper;

    public Video toEntity(VideoCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Video.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .visibility(request.getVisibility())
                .status(Status.UPLOADING)
                .build();
    }

    public Video toEntity(VideoCreateFrameRequest request) {
        if (request == null) {
            return null;
        }

        return Video.builder()
                .title(request.getTitle())
                .visibility(request.getVisibility())
                .status(request.getStatus())
                .build();
    }

    public VideoResponse toResponse(Video video) {
        if (video == null) {
            return null;
        }

        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .visibility(video.getVisibility())
                .status(video.getStatus())
                .user(video.getUser() != null ? userMapper.toResponse(video.getUser()) : null)
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .build();
    }

    public VideoFrameResponse toFrameResponse(Video video){
        if (video == null) {
            return null;
        }

        return VideoFrameResponse.builder()
                .videoId(video.getId())
                .build();
    }
}
