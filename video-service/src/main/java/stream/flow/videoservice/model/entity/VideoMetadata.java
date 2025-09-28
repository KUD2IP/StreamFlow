package stream.flow.videoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_metadata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoMetadata extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "file_size")
    private Long filesize;

    @Column(name = "bitrate")
    private Long bitrate;

    @Column(name = "resolution", length = 50)
    private String resolution;
}
