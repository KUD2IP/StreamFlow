package stream.flow.videoservice.mapper;

import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.response.VideoMetadataResponse;
import stream.flow.videoservice.model.entity.VideoMetadata;

@Component
public class VideoMetadataMapper {

    public VideoMetadataResponse toResponse(VideoMetadata metadata) {
        if (metadata == null) {
            return null;
        }

        return VideoMetadataResponse.builder()
                .duration(metadata.getDuration())
                .filesize(metadata.getFilesize())
                .bitrate(metadata.getBitrate())
                .resolution(metadata.getResolution())
                .build();
    }
}


