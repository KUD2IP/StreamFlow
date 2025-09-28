package stream.flow.videoservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import stream.flow.videoservice.model.document.VideoDocument;

public interface VideoSearchRepository extends ElasticsearchRepository<VideoDocument, String> {

    // Поиск по названию с пагинацией
    Page<VideoDocument> findByTitleContaining(String title, Pageable pageable);

    // Поиск по описанию с пагинацией
    Page<VideoDocument> findByDescriptionContaining(String description, Pageable pageable);

    // Поиск по категории с пагинацией
    Page<VideoDocument> findByCategory(String category, Pageable pageable);

    // Поиск по статусу с пагинацией
    Page<VideoDocument> findByStatus(String status, Pageable pageable);

    // Поиск по видимости с пагинацией
    Page<VideoDocument> findByVisibility(String visibility, Pageable pageable);

    // Поиск по пользователю с пагинацией
    Page<VideoDocument> findByUsername(String username, Pageable pageable);

    // Комбинированный поиск с пагинацией
    Page<VideoDocument> findByTitleContainingAndCategory(String title, String category, Pageable pageable);

    // Поиск по нескольким полям с пагинацией
    Page<VideoDocument> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);
}
