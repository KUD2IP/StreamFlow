package stream.flow.videoservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityUtils {

    /**
     * Получает ID пользователя из JWT токена
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authentication found in SecurityContext");
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Получаем subject (обычно это keycloak ID)
            String userId = jwt.getSubject();
            log.debug("Extracted userId from JWT: {}", userId);
            return userId;
        }

        log.warn("Principal is not a JWT token");
        return null;
    }

    /**
     * Получает username пользователя из JWT токена
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

    /**
     * Получает полное имя пользователя из JWT токена
     */
    public static String getCurrentUserFullName() {
        String firstName = getCurrentUserFirstName();
        String lastName = getCurrentUserLastName();
        
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        
        return null;
    }

    /**
     * Проверяет, аутентифицирован ли пользователь
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}




