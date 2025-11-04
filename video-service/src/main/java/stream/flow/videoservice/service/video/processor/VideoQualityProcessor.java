package stream.flow.videoservice.service.video.processor;

import stream.flow.videoservice.model.dto.response.VideoInfoResponse;
import stream.flow.videoservice.model.enums.Quality;

import java.util.UUID;

/**
 * Обрабатывает одно качество видео:
 * - Конвертация видео в нужное качество
 * - Извлечение метаданных
 * - Загрузка в хранилище
 * - Сохранение метаданных в БД
 */
public interface VideoQualityProcessor {

    /**
     * Обрабатывает видео для указанного качества
     * 
     * @param originalPath путь к оригинальному видео файлу
     * @param videoId ID видео
     * @param quality качество для обработки
     * @param outputPath путь для сохранения конвертированного файла
     * @return результат обработки (путь к файлу в хранилище и метаданные)
     */
    ProcessingResult processQuality(String originalPath, UUID videoId, Quality quality, String outputPath);

    /**
     * Результат обработки одного качества
     */
    record ProcessingResult(
            String storagePath,
            VideoInfoResponse metadata,
            String localFilePath
    ) {}
}

