package stream.flow.videoservice.exception.file;

public class VideoStreamNotFoundException extends RuntimeException {
    public VideoStreamNotFoundException(String message) {
        super(message);
    }
}
