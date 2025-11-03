package stream.flow.videoservice.mapper;

import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.response.TagResponse;
import stream.flow.videoservice.model.entity.VideoTags;

@Component
public class TagMapper {

    public TagResponse toResponse(VideoTags tag) {
        if (tag == null) {
            return null;
        }

        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}

