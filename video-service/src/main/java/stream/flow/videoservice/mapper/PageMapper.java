package stream.flow.videoservice.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import stream.flow.videoservice.model.dto.response.PageResponse;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PageMapper {

    public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        return PageResponse.<R>builder()
                .content(page.getContent().stream()
                        .map(mapper)
                        .collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}




