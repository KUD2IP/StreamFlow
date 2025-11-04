package stream.flow.videoservice.service.user;

import stream.flow.videoservice.model.dto.response.UserResponse;

public interface UserService {

    /**
     * Синхронизирует текущего пользователя из Keycloak
     * Вызывается явно через API /users/sync
     */
    UserResponse syncUser();

}
