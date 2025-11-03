package stream.flow.videoservice.model.dto.request;

import lombok.*;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.model.enums.Visibility;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoCreateFrameRequest {

    private String title;
    private Visibility visibility;
    private Status status;
    private String userId;
}
