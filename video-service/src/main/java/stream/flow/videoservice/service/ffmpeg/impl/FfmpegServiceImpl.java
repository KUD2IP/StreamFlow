package stream.flow.videoservice.service.ffmpeg.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stream.flow.videoservice.exception.file.FileOriginalNotFoundException;
import stream.flow.videoservice.exception.file.FileUploadException;
import stream.flow.videoservice.exception.file.VideoStreamNotFoundException;
import stream.flow.videoservice.model.dto.response.VideoInfoResponse;
import stream.flow.videoservice.model.enums.Quality;
import stream.flow.videoservice.service.ffmpeg.FfmpegService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис для работы с FFmpeg/FFprobe
 * Извлекает метаданные из видео файлов
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FfmpegServiceImpl implements FfmpegService {

    @Value("${ffmpeg.probe-path}")
    private String ffprobePath;

    @Value("${ffmpeg.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;

    @Value("${ffmpeg.conversion.preset:medium}")
    private String preset;

    @Value("${ffmpeg.conversion.crf:23}")
    private int crf;

    @Value("${ffmpeg.conversion.audio-bitrate:128}")
    private int audioBitrate;

    private final ObjectMapper objectMapper;

    /**
     * Извлекает метаданные из видео файла используя FFprobe
     * 
     * Как это работает:
     * 1. Запускаем команду ffprobe с параметрами для получения JSON
     * 2. FFprobe анализирует видео и возвращает информацию в JSON формате
     * 3. Парсим JSON и извлекаем нужные поля (длительность, разрешение, битрейт и т.д.)
     * 4. Возвращаем VideoInfoResponse с метаданными
     * 
     * @param pathOriginal путь к видео файлу
     * @return метаданные видео
     */
    @Override
    public VideoInfoResponse extractVideoMetadata(String pathOriginal) {
        log.info("Extracting metadata from video: {}", pathOriginal);

        // Проверяем существование файла
        if (!Files.exists(Paths.get(pathOriginal))) {
            throw new FileOriginalNotFoundException("Video file not found: " + pathOriginal);
        }

        try {
            // Команда для запуска ffprobe
            // -v quiet - убирает лишний вывод
            // -print_format json - формат вывода JSON
            // -show_format - показать информацию о файле (размер, битрейт и т.д.)
            // -show_streams - показать информацию о потоках (видео, аудио)
            ProcessBuilder processBuilder = new ProcessBuilder(
                ffprobePath,
                "-v", "quiet",
                "-print_format", "json",
                "-show_format",
                "-show_streams",
                pathOriginal
            );

            // Запускаем процесс
            Process process = processBuilder.start();

            // Читаем вывод команды (JSON)
            StringBuilder jsonOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonOutput.append(line);
                }
            }

            // Читаем ошибки (может быть полезно для отладки)
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line);
                }
            }

            // Ждем завершения процесса
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                log.error("FFprobe failed with exit code {}: {}", exitCode, errorOutput);
                throw new FileUploadException("Failed to extract video metadata: " + errorOutput);
            }

            // Парсим JSON
            JsonNode jsonNode = objectMapper.readTree(jsonOutput.toString());
            
            // Извлекаем метаданные
            VideoInfoResponse metadata = parseVideoMetadata(jsonNode, pathOriginal);
            
            log.info("Metadata extracted successfully: duration={}s, resolution={}, bitrate={}kbps",
                    metadata.getDuration(), metadata.getResolution(), metadata.getBitrateVideo());
            
            return metadata;

        } catch (IOException e) {
            log.error("IO error while extracting metadata: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to read video file: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Process interrupted while extracting metadata: {}", e.getMessage(), e);
            throw new FileUploadException("Metadata extraction interrupted", e);
        } catch (Exception e) {
            log.error("Error extracting metadata: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to extract video metadata: " + e.getMessage(), e);
        }
    }

    /**
     * Парсит JSON от FFprobe и извлекает нужные поля
     * 
     * Структура JSON от FFprobe:
     * {
     *   "format": {
     *     "duration": "123.45",
     *     "size": "12345678",
     *     "bit_rate": "5000000"
     *   },
     *   "streams": [
     *     {
     *       "codec_type": "video",
     *       "width": 1920,
     *       "height": 1080,
     *       "bit_rate": "4000000"
     *     },
     *     {
     *       "codec_type": "audio",
     *       "bit_rate": "128000"
     *     }
     *   ]
     * }
     */
    private VideoInfoResponse parseVideoMetadata(JsonNode jsonNode, String filePath) {
        JsonNode format = jsonNode.get("format");
        JsonNode streams = jsonNode.get("streams");

        // Извлекаем информацию о формате файла
        String durationStr = format.path("duration").asText("0");
        String sizeStr = format.path("size").asText("0");
        String bitrateStr = format.path("bit_rate").asText("0");

        // Ищем видео поток
        JsonNode videoStream = null;
        JsonNode audioStream = null;
        
        for (JsonNode stream : streams) {
            String codecType = stream.path("codec_type").asText("");
            if ("video".equals(codecType) && videoStream == null) {
                videoStream = stream;
            } else if ("audio".equals(codecType) && audioStream == null) {
                audioStream = stream;
            }
        }

        if (videoStream == null) {
            throw new VideoStreamNotFoundException("No video stream found in file");
        }

        // Извлекаем разрешение
        int width = videoStream.path("width").asInt(0);
        int height = videoStream.path("height").asInt(0);
        String resolution = width > 0 && height > 0 
            ? String.format("%dx%d", width, height) 
            : "unknown";

        // Извлекаем битрейт видео (может быть в stream или в format)
        String videoBitrateStr = videoStream.path("bit_rate").asText(bitrateStr);
        if (videoBitrateStr.isEmpty() || "0".equals(videoBitrateStr)) {
            videoBitrateStr = bitrateStr;
        }

        // Извлекаем битрейт аудио
        String audioBitrateStr = audioStream != null 
            ? audioStream.path("bit_rate").asText("128000") 
            : "128000";

        // Конвертируем строки в числа
        int duration = (int) Math.round(Double.parseDouble(durationStr));
        long fileSize = Long.parseLong(sizeStr);
        int bitrateVideo = Integer.parseInt(videoBitrateStr) / 1000; // конвертируем в kbps
        int bitrateAudio = Integer.parseInt(audioBitrateStr) / 1000; // конвертируем в kbps

        // Если размер файла не указан, получаем его из файловой системы
        if (fileSize == 0) {
            try {
                fileSize = Files.size(Paths.get(filePath));
            } catch (IOException e) {
                log.warn("Failed to get file size from filesystem: {}", e.getMessage());
            }
        }

        return VideoInfoResponse.builder()
                .duration(duration)
                .filesize(fileSize)
                .bitrateVideo(bitrateVideo)
                .bitrateAudio(bitrateAudio)
                .resolution(resolution)
                .build();
    }

    /**
     * Конвертирует видео в указанное качество используя FFmpeg
     * 
     * Параметры конвертации:
     * - P1080: 1920x1080, битрейт 5000k
     * - P720: 1280x720, битрейт 3000k
     * - P480: 854x480, битрейт 1500k
     * - P360: 640x360, битрейт 800k
     * - P240: 426x240, битрейт 400k
     * 
     * @param inputPath путь к исходному видео файлу
     * @param outputPath путь к выходному файлу
     * @param quality качество для конвертации
     * @return путь к конвертированному файлу
     */
    @Override
    public String convertQualityVideo(String inputPath, String outputPath, Quality quality) {
        log.info("Converting video: {} to quality: {}, output: {}", inputPath, quality, outputPath);

        // Проверяем существование исходного файла
        if (!Files.exists(Paths.get(inputPath))) {
            throw new FileOriginalNotFoundException("Input video file not found: " + inputPath);
        }

        // Получаем параметры конвертации для качества
        ConversionParams params = getConversionParams(quality);

        // Создаем директорию для выходного файла, если нужно
        Path outputFilePath = Paths.get(outputPath);
        try {
            Files.createDirectories(outputFilePath.getParent());
        } catch (IOException e) {
            log.error("Failed to create output directory: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to create output directory: " + e.getMessage(), e);
        }

        try {
            // Команда для конвертации видео:
            // -i input.mp4 - входной файл
            // -vf scale=WIDTH:HEIGHT - изменение разрешения
            // -b:v BITRATEk - битрейт видео
            // -c:v libx264 - видеокодек H.264
            // -preset PRESET - пресет для скорости/качества конвертации
            // -crf CRF - Constant Rate Factor (18-28, меньше = лучше качество)
            // -c:a aac - аудиокодек AAC
            // -b:a BITRATEk - битрейт аудио
            // -y - перезаписать выходной файл если существует
            // output.mp4 - выходной файл
            ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpegPath,
                "-i", inputPath,
                "-f", "mp4",
                "-vf", "scale=" + params.width + ":" + params.height,
                "-b:v", params.videoBitrate + "k",
                "-c:v", "libx264",
                "-preset", preset,
                "-crf", String.valueOf(crf),
                "-c:a", "aac",
                "-b:a", audioBitrate + "k",
                "-y", // перезаписать выходной файл если существует
                outputPath
            );

            log.debug("FFmpeg command: {}", String.join(" ", processBuilder.command()));

            // Запускаем процесс
            Process process = processBuilder.start();

            // Читаем вывод (FFmpeg пишет прогресс в stderr)
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                    // Логируем прогресс (FFmpeg выводит время в формате time=00:01:23.45)
                    if (line.contains("time=")) {
                        String timeInfo = extractTimeFromProgress(line);
                        if (timeInfo != null) {
                            log.debug("Conversion progress: {}", timeInfo);
                        }
                    }
                }
            }

            // Ждем завершения процесса
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.error("FFmpeg conversion failed with exit code {}: {}", exitCode, errorOutput);
                throw new FileUploadException("Failed to convert video: " + errorOutput);
            }

            // Проверяем, что выходной файл создан
            if (!Files.exists(outputFilePath)) {
                throw new FileUploadException("Output file was not created: " + outputPath);
            }

            long outputFileSize = Files.size(outputFilePath);
            log.info("Video converted successfully: {} -> {} (size: {} bytes, quality: {})",
                    inputPath, outputPath, outputFileSize, quality);

            return outputPath;

        } catch (IOException e) {
            log.error("IO error while converting video: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to convert video: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Process interrupted while converting video: {}", e.getMessage(), e);
            throw new FileUploadException("Video conversion interrupted", e);
        } catch (Exception e) {
            log.error("Error converting video: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to convert video: " + e.getMessage(), e);
        }
    }

    /**
     * Извлекает информацию о времени из строки прогресса FFmpeg
     * Формат: time=00:01:23.45
     */
    private String extractTimeFromProgress(String line) {
        try {
            int timeIndex = line.indexOf("time=");
            if (timeIndex != -1) {
                int endIndex = line.indexOf(" ", timeIndex);
                if (endIndex == -1) {
                    endIndex = line.length();
                }
                return line.substring(timeIndex, endIndex);
            }
        } catch (Exception e) {
            // Игнорируем ошибки парсинга
        }
        return null;
    }

    /**
     * Параметры конвертации для качества
     *
     * @param videoBitrate в kbps
     */
        private record ConversionParams(int width, int height, int videoBitrate) {
    }

    /**
     * Возвращает параметры конвертации для указанного качества
     */
    private ConversionParams getConversionParams(Quality quality) {
        return switch (quality) {
            case P1080 -> new ConversionParams(1920, 1080, 5000);
            case P720 -> new ConversionParams(1280, 720, 3000);
            case P480 -> new ConversionParams(854, 480, 1500);
            case P360 -> new ConversionParams(640, 360, 800);
            case P240 -> new ConversionParams(426, 240, 400);
        };
    }
}
