package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import stream.flow.videoservice.service.video.AsyncProcessVideoService;
import stream.flow.videoservice.service.video.VideoProcessingService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncProcessVideoServiceImpl implements AsyncProcessVideoService {

    private final VideoProcessingService videoProcessingService;

    @Override
    @Async(value = "taskExecutor")
    public void processingAsync(String pathOriginal, UUID videoId) {
        log.info("Processing video frame request: {}", pathOriginal);

        videoProcessingService.processing(pathOriginal, videoId);

        log.info("Processing video frame response: {}", pathOriginal);

    }
}
