package stream.flow.videoservice.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.exception.user.RequiredJwtException;
import stream.flow.videoservice.exception.user.UnauthorizedAccessException;
import stream.flow.videoservice.mapper.UserMapper;
import stream.flow.videoservice.model.dto.response.UserResponse;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.repository.UsersRepository;
import stream.flow.videoservice.service.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UsersRepository usersRepository;

    /**
     * Получает текущего пользователя из базы данных
     * Если пользователь не найден - пытается синхронизировать из JWT
     */
    @Override
    @Transactional
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("No authentication found for user sync");
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new UnauthorizedAccessException("Principal is not a JWT token for user sync");
        }

        String keycloakId = jwt.getSubject();
        if (keycloakId == null) {
            throw new RequiredJwtException("Missing required user data in JWT token");
        }

        // Сначала пытаемся найти пользователя в базе
        Users user = usersRepository.findByKeycloakId(keycloakId).orElse(null);

        if (user == null) {
            // Пользователь не найден - синхронизируем
            log.debug("User not found in database, attempting sync for keycloakId: {}", keycloakId);
            return syncCurrentUser(jwt);
        }
        
        // Пользователь найден - возвращаем его данные
        return userMapper.toResponse(user);
    }

    private UserResponse syncCurrentUser(Jwt jwt) {

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
            throw new RequiredJwtException("Missing required user data in JWT token");
        }

        // Проверяем, существует ли пользователь (дополнительная проверка на случай race condition)
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
                return userMapper.toResponse(usersRepository.save(existingUser));
            } else {
                log.debug("User data is up to date for keycloakId: {}", keycloakId);
                return userMapper.toResponse(existingUser);
            }
        } else {
            // Пользователь не существует - создаем новую запись (JIT Provisioning)
            Users newUser = Users.builder()
                    .keycloakId(keycloakId)
                    .username(username)
                    .email(email)
                    .firstName(firstName != null ? firstName : "Unknown")
                    .lastName(lastName != null ? lastName : "Unknown")
                    .build();

            log.info("Created new user via JIT provisioning: keycloakId={}, username={}",
                    keycloakId, username);

            return userMapper.toResponse(usersRepository.save(newUser));
        }
    }
}
