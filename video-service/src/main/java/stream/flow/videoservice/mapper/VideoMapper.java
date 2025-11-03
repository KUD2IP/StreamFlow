package stream.flow.videoservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
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

    public VideoResponse toResponse(Video video) {
        if (video == null) {
            return null;
        }

        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .visibility(video.getVisibility())
                .filename(video.getFilename())
                .videoUrl(video.getVideoUrl())
                .previewUrl(video.getPreviewUrl())
                .status(video.getStatus())
                .user(video.getUser() != null ? userMapper.toResponse(video.getUser()) : null)
                .metadata(video.getMetadata() != null ? videoMetadataMapper.toResponse(video.getMetadata()) : null)
                .analytics(video.getAnalytics() != null ? videoAnalyticsMapper.toResponse(video.getAnalytics()) : null)
                .tags(video.getTags() != null ? video.getTags().stream().map(tagMapper::toResponse).toList() : null)
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .build();
    }
}
