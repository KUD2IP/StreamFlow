package stream.flow.videoservice.mapper;

import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.response.VideoAnalyticsResponse;
import stream.flow.videoservice.model.entity.VideoAnalytics;

@Component
public class VideoAnalyticsMapper {

    public VideoAnalyticsResponse toResponse(VideoAnalytics analytics) {
        if (analytics == null) {
            return null;
        }

        return VideoAnalyticsResponse.builder()
                .viewsCount(analytics.getViewsCount())
                .likesCount(analytics.getLikesCount())
                .commentCount(analytics.getCommentsCount())
                .build();
    }
}


