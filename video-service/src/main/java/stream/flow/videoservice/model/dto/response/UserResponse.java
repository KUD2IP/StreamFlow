package stream.flow.videoservice.model.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String keycloakId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
}



