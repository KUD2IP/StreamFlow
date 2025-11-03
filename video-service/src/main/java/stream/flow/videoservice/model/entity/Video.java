package stream.flow.videoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import stream.flow.videoservice.model.enums.Status;
import stream.flow.videoservice.model.enums.Visibility;

import java.util.List;

@Entity
@Table(name = "video")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video extends BaseEntity {

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "visibility", nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.UPLOADING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VideoQuality> qualities;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL)
    private VideoAnalytics analytics;

    @ManyToMany
    @JoinTable(
            name = "video_tag",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
}
