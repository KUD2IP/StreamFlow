package stream.flow.videoservice.exception.user;

public class RequiredJwtException extends RuntimeException {

    public RequiredJwtException(String message) {
        super(message);
    }
}
