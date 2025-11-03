package stream.flow.videoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stream.flow.videoservice.model.entity.VideoAnalytics;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoAnalyticsRepository extends JpaRepository<VideoAnalytics, UUID> {
    
    Optional<VideoAnalytics> findByVideoId(UUID videoId);
}
