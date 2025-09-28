package stream.flow.videoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stream.flow.videoservice.model.entity.Video;

import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID>{
}
