package stream.flow.videoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((authorize) -> authorize
                    .requestMatchers("/actuator/**").permitAll()
                    // Публичный доступ к просмотру видео
                    .requestMatchers("/api/v1/videos", "/api/v1/videos/search/**", "/api/v1/videos/category/**").permitAll()
                    .requestMatchers("/api/v1/videos/**").permitAll() // GET запросы к конкретному видео
                    // Публичный доступ к категориям и тегам
                    .requestMatchers("/api/v1/categories/**", "/api/v1/tags/**").permitAll()
                    // Остальные операции требуют аутентификации
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer((oauth2) -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
            
            // Извлекаем роли из realm_access
            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                @SuppressWarnings("unchecked")
                var roles = (List<String>) realmAccess.get("roles");
                if (roles != null) {
                    var realmAuthorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .map(GrantedAuthority.class::cast)
                            .toList();
                    
                    return Stream.concat(authorities.stream(), realmAuthorities.stream()).toList();
                }
            }
            
            return authorities;
        });
        return jwtAuthenticationConverter;
    }
}
