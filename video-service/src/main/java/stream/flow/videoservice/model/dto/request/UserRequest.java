package stream.flow.videoservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для передачи данных пользователя в API
 * Содержит только необходимые для клиента поля
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    
    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

}
