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
    
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Создает DTO только с базовой информацией для синхронизации
     */
    public static UserRequest forSync(UUID id, String username) {
        return UserRequest.builder()
                .id(id)
                .username(username)
                .build();
    }
}
