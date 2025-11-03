package stream.flow.videoservice.mapper;

import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.response.VideoQualityResponse;
import stream.flow.videoservice.model.entity.VideoQuality;

@Component
public class VideoMetadataMapper {

    public VideoQualityResponse toResponse(VideoQuality quality) {
        if (quality == null) {
            return null;
        }

        return VideoQualityResponse.builder()
                .duration(quality.getDuration())
                .filesize(quality.getFileSize())
                .bitrateVideo(quality.getBitrateVideo())
                .bitrateAudio(quality.getBitrateAudio())
                .resolution(quality.getResolution())
                .build();
    }
}


