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

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "visibility", nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "filename", nullable = false, length = 500)
    private String filename;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "preview_url", nullable = false)
    private String previewUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.UPLOADING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Categories category;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL)
    private VideoMetadata metadata;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL)
    private VideoAnalytics analytics;

    @ManyToMany
    @JoinTable(
            name = "video_tag",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<VideoTags> tags;
}
