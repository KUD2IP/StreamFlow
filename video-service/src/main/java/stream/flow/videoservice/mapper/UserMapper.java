package stream.flow.videoservice.mapper;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import stream.flow.videoservice.exception.user.PrincipalNullException;
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
                .keycloakId(user.getKeycloakId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public Users toEntity(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }

        if (userRequest.getKeycloakId() == null
        || userRequest.getUsername() == null) {
            throw new PrincipalNullException("Keycloak ID or Username is null");
        }

        return Users.builder()
                .keycloakId(userRequest.getKeycloakId())
                .email(userRequest.getEmail())
                .username(userRequest.getUsername())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .build();
    }
}