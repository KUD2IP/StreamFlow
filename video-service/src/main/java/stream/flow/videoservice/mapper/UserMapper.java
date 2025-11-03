package stream.flow.videoservice.mapper;

import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.request.UserRequest;
import stream.flow.videoservice.model.dto.response.UserResponse;
import stream.flow.videoservice.model.entity.Users;

/**
 * Маппер для конвертации между User Entity и User DTOs
 */
@Component
public class UserMapper {

    public UserResponse toResponse(Users user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}