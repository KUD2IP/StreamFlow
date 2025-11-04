package stream.flow.videoservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import stream.flow.videoservice.model.dto.request.UserRequest;

import java.util.UUID;

/**
 * Утилитный класс для работы с Spring Security контекстом.
 * Использует статические методы для удобного доступа к информации о текущем пользователе.
 * 
 * Все методы работают с SecurityContextHolder, который является потокобезопасным
 * и хранит контекст безопасности в ThreadLocal.
 */
@Slf4j
public final class SecurityUtils {

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитного класса.
     */
    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Получает ID пользователя из JWT токена
     * 
     * @return ID пользователя или null, если пользователь не аутентифицирован
     *         или токен не содержит subject
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authentication found in SecurityContext");
            throw new AuthenticationServiceException("No authentication found in SecurityContext");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Получаем subject (обычно это keycloak ID)
            String userId = jwt.getSubject();
            log.debug("Extracted userId from JWT: {}", userId);
            return userId;
        }

        log.warn("Principal is not a JWT token");
        throw new AuthenticationServiceException("Principal is not a JWT token");
    }

    /**
     * Получает username пользователя из JWT токена
     * 
     * @return username пользователя или null, если пользователь не аутентифицирован
     *         или токен не содержит preferred_username
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("preferred_username");
        }

        return null;
    }

    /**
     * Получает email пользователя из JWT токена
     * 
     * @return email пользователя или null, если пользователь не аутентифицирован
     *         или токен не содержит email
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("email");
        }

        return null;
    }

    /**
     * Получает имя пользователя из JWT токена
     * 
     * @return имя пользователя или null, если пользователь не аутентифицирован
     *         или токен не содержит given_name
     */
    public static String getCurrentUserFirstName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("given_name");
        }

        return null;
    }

    /**
     * Получает фамилию пользователя из JWT токена
     * 
     * @return фамилия пользователя или null, если пользователь не аутентифицирован
     *         или токен не содержит family_name
     */
    public static String getCurrentUserLastName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("family_name");
        }

        return null;
    }

    public static UserRequest getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationServiceException("No authentication found in SecurityContext");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
           return UserRequest.builder()
                   .keycloakId(jwt.getSubject())
                   .email(jwt.getClaim("email"))
                   .username(jwt.getClaim("preferred_username"))
                   .firstName(jwt.getClaim("given_name"))
                   .lastName( jwt.getClaim("family_name"))
                   .build();
        }

        throw new AuthenticationServiceException("Principal is not a JWT token");
    }

    /**
     * Проверяет, аутентифицирован ли пользователь
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}




