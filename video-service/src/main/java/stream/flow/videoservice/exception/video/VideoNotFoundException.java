package stream.flow.videoservice.exception.video;

import java.util.UUID;

public class VideoNotFoundException extends RuntimeException {
    
    public VideoNotFoundException(String message) {
        super(message);
    }

    public VideoNotFoundException(UUID videoId) {
        super("Video not found with id: " + videoId);
    }
}


