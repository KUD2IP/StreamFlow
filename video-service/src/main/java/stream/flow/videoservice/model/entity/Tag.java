package stream.flow.videoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseEntity {

    @Column(name = "name", nullable = false, length = 50)
    private String name;
}

