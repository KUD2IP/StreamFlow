package stream.flow.videoservice.mapper;

import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.response.TagResponse;
import stream.flow.videoservice.model.entity.Tag;

@Component
public class TagMapper {

    public TagResponse toResponse(Tag tag) {
        if (tag == null) {
            return null;
        }

        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}

