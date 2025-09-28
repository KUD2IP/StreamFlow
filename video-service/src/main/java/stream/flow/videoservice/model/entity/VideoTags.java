package stream.flow.videoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "video_tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTags extends BaseEntity {

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Video> videos;
}
