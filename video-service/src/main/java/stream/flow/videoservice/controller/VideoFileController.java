package stream.flow.videoservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.model.dto.response.VideoUploadResponse;
import stream.flow.videoservice.service.video.VideoUploadService;
import stream.flow.videoservice.util.SecurityUtils;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/videos/upload")
@RequiredArgsConstructor
public class VideoFileController {

    private final VideoUploadService videoUploadService;


    /**
     * Загрузка видео файла
     * POST /api/v1/videos/upload/{id}
     */
    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        log.info("Uploading video file for videoId: {}, size: {} bytes", id, file.getSize());

        String userId = SecurityUtils.getCurrentUserId();
        VideoUploadResponse response = videoUploadService.uploadVideo(id, file, userId);

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
