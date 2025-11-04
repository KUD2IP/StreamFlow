package stream.flow.videoservice.mapper;

import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.response.VideoInfoResponse;
import stream.flow.videoservice.model.entity.VideoQuality;

@Component
public class VideoQualityMapper {

    public VideoInfoResponse toResponse(VideoQuality quality) {
        if (quality == null) {
            return null;
        }

        return VideoInfoResponse.builder()
                .duration(quality.getDuration())
                .filesize(quality.getFileSize())
                .bitrateVideo(quality.getBitrateVideo())
                .bitrateAudio(quality.getBitrateAudio())
                .resolution(quality.getResolution())
                .build();
    }

    public VideoQuality toEntity(VideoInfoResponse response) {
        if (response == null) {
            return null;
        }

        return VideoQuality.builder()
                .duration(response.getDuration())
                .fileSize(response.getFilesize())
                .bitrateVideo(response.getBitrateVideo())
                .bitrateAudio(response.getBitrateAudio())
                .resolution(response.getResolution())
                .build();
    }
}


