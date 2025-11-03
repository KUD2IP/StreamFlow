package stream.flow.videoservice.model.dto.response;

import lombok.*;
import stream.flow.videoservice.model.enums.Status;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoUploadResponse {

    private UUID videoId;
    private Status status;
    private String message;
}
