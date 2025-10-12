package stream.flow.videoservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.repository.UsersRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UsersRepository usersRepository;

    /**
     * Синхронизирует пользователя из JWT токена в базу данных
     * Если пользователь не существует - создает новую запись (JIT Provisioning)
     * Если существует - обновляет данные
     */
    @Transactional
    public Users syncCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authentication found for user sync");
            return null;
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            log.warn("Principal is not a JWT token for user sync");
            return null;
        }

        // Извлекаем данные из JWT токена
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");

        log.debug("Syncing user with keycloakId: {}, username: {}, email: {}", 
                 keycloakId, username, email);

        if (keycloakId == null || username == null || email == null) {
            log.error("Missing required user data in JWT token: keycloakId={}, username={}, email={}", 
                     keycloakId, username, email);
            return null;
        }

        // Проверяем, существует ли пользователь
        Users existingUser = usersRepository.findByKeycloakId(keycloakId).orElse(null);
        
        if (existingUser != null) {
            // Пользователь существует - обновляем данные
            boolean updated = false;
            
            if (!username.equals(existingUser.getUsername())) {
                existingUser.setUsername(username);
                updated = true;
            }
            
            if (!email.equals(existingUser.getEmail())) {
                existingUser.setEmail(email);
                updated = true;
            }
            
            if (firstName != null && !firstName.equals(existingUser.getFirstName())) {
                existingUser.setFirstName(firstName);
                updated = true;
            }
            
            if (lastName != null && !lastName.equals(existingUser.getLastName())) {
                existingUser.setLastName(lastName);
                updated = true;
            }
            
            if (updated) {
                log.info("Updated user data for keycloakId: {}", keycloakId);
                return usersRepository.save(existingUser);
            } else {
                log.debug("User data is up to date for keycloakId: {}", keycloakId);
                return existingUser;
            }
        } else {
            // Пользователь не существует - создаем новую запись (JIT Provisioning)
            Users newUser = Users.builder()
                    .keycloakId(keycloakId)
                    .username(username)
                    .email(email)
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .build();
            
            log.info("Created new user via JIT provisioning: keycloakId={}, username={}", 
                    keycloakId, username);
            
            return usersRepository.save(newUser);
        }
    }

    /**
     * Получает текущего пользователя из базы данных
     * Если пользователь не найден - пытается синхронизировать из JWT
     */
    @Transactional
    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        String keycloakId = jwt.getSubject();
        if (keycloakId == null) {
            return null;
        }

        // Сначала пытаемся найти пользователя в базе
        Users user = usersRepository.findByKeycloakId(keycloakId).orElse(null);
        
        if (user == null) {
            // Пользователь не найден - синхронизируем
            log.debug("User not found in database, attempting sync for keycloakId: {}", keycloakId);
            user = syncCurrentUser();
        }
        
        return user;
    }

    /**
     * Получает ID текущего пользователя
     * Если пользователь не найден - пытается синхронизировать
     */
    @Transactional
    public UUID getCurrentUserId() {
        Users user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
