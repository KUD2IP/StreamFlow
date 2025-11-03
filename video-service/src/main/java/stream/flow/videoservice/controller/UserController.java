package stream.flow.videoservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stream.flow.videoservice.model.dto.response.UserResponse;
import stream.flow.videoservice.service.user.UserService;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Синхронизирует текущего пользователя из Keycloak
     * Вызывается фронтендом автоматически после входа в Keycloak
     */
    @PostMapping("/sync")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> syncCurrentUser() {
        log.info("Syncing current user from Keycloak");
        
        UserResponse userResponse = userService.getCurrentUser();

        log.info("Successfully synced user: {}", userResponse.getUsername());
        return ResponseEntity.ok(userResponse);
    }
}
