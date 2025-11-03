package stream.flow.videoservice.model.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoAnalyticsResponse {

    private Long viewsCount;
    private Long likesCount;
    private Long commentCount;
}
