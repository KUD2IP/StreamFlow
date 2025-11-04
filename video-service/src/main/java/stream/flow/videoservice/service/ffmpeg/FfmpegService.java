package stream.flow.videoservice.service.ffmpeg;

import stream.flow.videoservice.model.dto.response.VideoInfoResponse;
import stream.flow.videoservice.model.enums.Quality;

public interface FfmpegService {

    VideoInfoResponse extractVideoMetadata(String pathOriginal);

    /**
     * Конвертирует видео в указанное качество
     * 
     * @param inputPath путь к исходному видео файлу
     * @param outputPath путь к выходному файлу
     * @param quality качество для конвертации (P1080, P720, P480, P360, P240)
     * @return путь к конвертированному файлу
     */
    String convertQualityVideo(String inputPath, String outputPath, Quality quality);
}
