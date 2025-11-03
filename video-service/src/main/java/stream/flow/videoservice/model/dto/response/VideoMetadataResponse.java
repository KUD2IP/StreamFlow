package stream.flow.videoservice.model.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoMetadataResponse {

    private Integer duration;
    private Long filesize;
    private Long bitrate;
    private String resolution;
}
