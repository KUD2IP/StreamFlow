package stream.flow.videoservice.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.exception.file.InvalidFileTypeException;
import stream.flow.videoservice.exception.file.FileSizeExceededException;
import stream.flow.videoservice.exception.user.UnauthorizedAccessException;
import stream.flow.videoservice.exception.video.VideoNotFoundException;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.repository.VideoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoValidationService {

    private final VideoRepository videoRepository;
    private final FileUtilService fileUtilService;

    @Value("${file.upload.max-size:10485760000}") // 10GB default
    private long maxFileSize;

    @Value("${file.upload.allowed-video-types:mp4,avi,mov,mkv,webm}")
    private String allowedVideoTypes;

    @Value("${file.upload.allowed-image-types:jpg,jpeg,png,gif,webp}")
    private String allowedImageTypes;

    /**
     * Валидирует видео файл
     */
    public void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("Video file is required");
        }

        validateFileSize(file, maxFileSize);
        
        List<String> allowedTypes = Arrays.asList(allowedVideoTypes.split(","));
        validateFileType(file, allowedTypes);
        
        log.info("Video file validation passed: {}", file.getOriginalFilename());
    }

    /**
     * Валидирует файл превью (thumbnail)
     */
    public void validateThumbnailFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("Thumbnail file is required");
        }

        // Для изображений ограничение 10MB
        long maxImageSize = 10 * 1024 * 1024; // 10MB
        validateFileSize(file, maxImageSize);
        
        List<String> allowedTypes = Arrays.asList(allowedImageTypes.split(","));
        validateFileType(file, allowedTypes);
        
        log.info("Thumbnail file validation passed: {}", file.getOriginalFilename());
    }

    /**
     * Валидирует размер файла
     */
    public void validateFileSize(MultipartFile file, long maxSize) {
        if (file.getSize() > maxSize) {
            long maxSizeMB = maxSize / (1024 * 1024);
            long fileSizeMB = file.getSize() / (1024 * 1024);
            throw new FileSizeExceededException(
                String.format("File size %d MB exceeds maximum allowed size %d MB", fileSizeMB, maxSizeMB)
            );
        }
    }

    /**
     * Валидирует тип файла по расширению
     */
    public void validateFileType(MultipartFile file, List<String> allowedTypes) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new InvalidFileTypeException("Filename is empty");
        }

        String extension = fileUtilService.getFileExtension(filename);
        
        if (!allowedTypes.contains(extension)) {
            throw new InvalidFileTypeException(
                String.format("File type '%s' is not allowed. Allowed types: %s", 
                    extension, String.join(", ", allowedTypes))
            );
        }
    }

    /**
     * Проверяет, что пользователь является владельцем видео
     */
    public void validateUserOwnership(UUID videoId, String userId) {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new VideoNotFoundException("Video not found with id: " + videoId));

        if (video.getUser() == null || !video.getUser().getKeycloakId().equals(userId)) {
            throw new UnauthorizedAccessException("User is not authorized to modify this video");
        }
    }

    /**
     * Проверяет существование видео
     */
    public Video validateVideoExists(UUID videoId) {
        return videoRepository.findById(videoId)
            .orElseThrow(() -> new VideoNotFoundException("Video not found with id: " + videoId));
    }
}



