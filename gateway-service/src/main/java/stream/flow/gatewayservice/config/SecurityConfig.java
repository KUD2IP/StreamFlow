package stream.flow.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/test/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/api/videos/public/**").permitAll()
                .pathMatchers("/api/recommendations/public/**").permitAll()
                .pathMatchers("/api/v1/videos/**").authenticated()
                .pathMatchers("/api/v1/users/**").authenticated()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Разрешенные источники
        corsConfig.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://127.0.0.1:5173"
        ));
        
        // Разрешенные методы
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Разрешенные заголовки
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        // Разрешить отправку credentials
        corsConfig.setAllowCredentials(true);
        
        // Заголовки для клиента
        corsConfig.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
        
        // Максимальное время кэширования
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return source;
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new SimpleJwtGrantedAuthoritiesConverter());
        return converter;
    }

    /**
     * Простой конвертер для извлечения ролей из JWT токена.
     * Ищет роли в claim "realm_access.roles".
     */
    public static class SimpleJwtGrantedAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
        
        @Override
        public Flux<GrantedAuthority> convert(Jwt jwt) {
            // Извлекаем роли из realm_access
            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                @SuppressWarnings("unchecked")
                var roles = (List<String>) realmAccess.get("roles");
                if (roles != null) {
                    return Flux.fromIterable(roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .map(GrantedAuthority.class::cast)
                            .toList());
                }
            }
            
            return Flux.empty();
        }
    }

}
