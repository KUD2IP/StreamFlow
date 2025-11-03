package stream.flow.videoservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stream.flow.videoservice.model.entity.Users;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.model.enums.Visibility;

import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    
    Page<Video> findByVisibilityAndStatus(Visibility visibility, Status status, Pageable pageable);
    
    Page<Video> findByUser(Users user, Pageable pageable);
    
    Page<Video> findByUserAndStatus(Users user, Status status, Pageable pageable);
}
