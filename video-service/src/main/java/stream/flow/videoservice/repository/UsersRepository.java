package stream.flow.videoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stream.flow.videoservice.model.entity.Users;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
    
    Optional<Users> findByKeycloakId(String keycloakId);

    boolean existsByKeycloakId(String keycloakId);
    
    Optional<Users> findByUsername(String username);
    
    Optional<Users> findByEmail(String email);
}
