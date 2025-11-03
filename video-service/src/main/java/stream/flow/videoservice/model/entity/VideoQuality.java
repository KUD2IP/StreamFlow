package stream.flow.videoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import stream.flow.videoservice.model.enums.Quality;

@Entity
@Table(name = "video_quality")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoQuality extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "quality_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private Quality quality;

    @Column(name = "storage_path", nullable = false, length = 1000)
    private String storagePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "bitrate_video")
    private Integer bitrateVideo;

    @Column(name = "bitrate_audio")
    private Integer bitrateAudio;

    @Column(name = "resolution", nullable = false, length = 20)
    private String resolution;
}
