package stream.flow.videoservice.model.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoInfoResponse {

    private Integer duration;
    private Long filesize;
    private Integer bitrateVideo;
    private Integer bitrateAudio;
    private String resolution;
}
