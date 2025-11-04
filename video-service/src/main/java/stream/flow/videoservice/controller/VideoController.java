package stream.flow.videoservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.model.dto.response.VideoUploadResponse;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.service.video.VideoUploadService;
import stream.flow.videoservice.util.SecurityUtils;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final VideoUploadService videoUploadService;

    /**
     * Создание нового видео (только метаданные)
     * POST /api/v1/videos
     */
    @PostMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoResponse> createVideo(@Valid @RequestBody VideoCreateRequest request, @PathVariable UUID videoId) {
        log.info("Creating video with title: {}", request.getTitle());

        String userId = SecurityUtils.getCurrentUserId();
        VideoResponse response = videoService.createVideo(request, userId, videoId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Загрузка видео файла
     * POST /api/v1/videos/upload/{id}
     */
    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Uploading video file, size: {} bytes", file.getSize());

        String userId = SecurityUtils.getCurrentUserId();
        VideoUploadResponse response = videoUploadService.uploadVideo(file, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Загрузка превью (thumbnail)
     * POST /api/v1/videos/{id}/thumbnail
     */
    @PostMapping(value = "/{id}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoUploadResponse> uploadThumbnail(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        log.info("Uploading thumbnail for video: {}", id);

        String userId = SecurityUtils.getCurrentUserId();
        VideoUploadResponse response = videoUploadService.uploadThumbnail(id, file, userId);

        return ResponseEntity.ok(response);
    }
}




