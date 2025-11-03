package stream.flow.videoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_analytics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoAnalytics extends BaseEntity {

    @Column(name = "views_count")
    @Builder.Default
    private Long viewsCount = 0L;

    @Column(name = "likes_count")
    @Builder.Default
    private Long likesCount = 0L;

    @Column(name = "comments_count")
    @Builder.Default
    private Long commentsCount = 0L;

    @OneToOne
    @JoinColumn(name = "video_id")
    private Video video;

}
