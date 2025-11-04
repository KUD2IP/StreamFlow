package stream.flow.videoservice.service.video;

import java.util.UUID;

public interface AsyncProcessVideoService {

    void processingAsync(String pathOriginal,  UUID videoId);
}
