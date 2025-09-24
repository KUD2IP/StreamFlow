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
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/test/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/api/videos/public/**").permitAll()
                .pathMatchers("/api/recommendations/public/**").permitAll()
                .pathMatchers("/api/videos/**").authenticated()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
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
