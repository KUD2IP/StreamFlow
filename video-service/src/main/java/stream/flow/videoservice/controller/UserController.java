package stream.flow.videoservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.service.UserSyncService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserSyncService userSyncService;

    /**
     * Синхронизирует текущего пользователя из Keycloak
     * Вызывается фронтендом автоматически после входа в Keycloak
     */
    @PostMapping("/sync")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> syncCurrentUser() {
        log.info("Syncing current user from Keycloak");
        
        Users user = userSyncService.syncCurrentUser();
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to sync user"));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("synced", true);

        log.info("Successfully synced user: {}", user.getUsername());
        return ResponseEntity.ok(userInfo);
    }
}
