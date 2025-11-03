package stream.flow.videoservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stream.flow.videoservice.model.dto.request.VideoCreateRequest;
import stream.flow.videoservice.model.dto.response.VideoResponse;
import stream.flow.videoservice.service.video.VideoService;
import stream.flow.videoservice.util.SecurityUtils;

@Slf4j
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * Создание нового видео (только метаданные)
     * POST /api/v1/videos
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoResponse> createVideo(@Valid @RequestBody VideoCreateRequest request) {
        log.info("Creating video with title: {}", request.getTitle());
        
        String userId = SecurityUtils.getCurrentUserId();
        VideoResponse response = videoService.createVideo(request, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}




