package stream.flow.videoservice.model.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "streamflow-videos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String title;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String description;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String visibility;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
}
