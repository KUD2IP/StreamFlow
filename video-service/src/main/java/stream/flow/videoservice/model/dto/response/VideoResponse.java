package stream.flow.videoservice.model.dto.response;

import lombok.*;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.model.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResponse {

    private UUID id;
    private String title;
    private String description;
    private Visibility visibility;
    private Status status;
    
    private UserResponse user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
