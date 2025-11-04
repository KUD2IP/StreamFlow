package stream.flow.videoservice.service.video;

import stream.flow.videoservice.model.dto.response.VideoInfoResponse;
import stream.flow.videoservice.model.enums.Quality;

import java.util.UUID;

public interface VideoQualityService {
    void saveVideoMetadata(VideoInfoResponse videoInfoResponse, String storagePath, UUID videoId, Quality quality);
}
