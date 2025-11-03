package stream.flow.videoservice.service.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class FileUtilService {

    /**
     * Генерирует уникальное имя файла на основе UUID и оригинального расширения
     */
    public String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uniqueId = UUID.randomUUID().toString();
        return extension.isEmpty() ? uniqueId : uniqueId + "." + extension;
    }

    /**
     * Извлекает расширение файла из имени
     */
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Определяет Content-Type на основе расширения файла
     */
    public String getContentType(String filename) {
        String extension = getFileExtension(filename);
        
        return switch (extension) {
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mov" -> "video/quicktime";
            case "mkv" -> "video/x-matroska";
            case "webm" -> "video/webm";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    /**
     * Проверяет, является ли файл видео
     */
    public boolean isVideoFile(String filename) {
        String extension = getFileExtension(filename);
        return extension.matches("mp4|avi|mov|mkv|webm");
    }

    /**
     * Проверяет, является ли файл изображением
     */
    public boolean isImageFile(String filename) {
        String extension = getFileExtension(filename);
        return extension.matches("jpg|jpeg|png|gif|webp");
    }
}




