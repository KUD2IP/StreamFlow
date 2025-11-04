package stream.flow.videoservice.service.video;

import java.util.UUID;

public interface VideoProcessingService {

    void processing(String pathOriginal, UUID videoId);

}
