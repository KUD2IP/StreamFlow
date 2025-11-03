package stream.flow.videoservice.model.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoFrameResponse {
    private UUID videoId;
}
