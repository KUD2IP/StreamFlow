package stream.flow.videoservice.service.video.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stream.flow.videoservice.exception.video.VideoNotFoundException;
import stream.flow.videoservice.mapper.VideoQualityMapper;
import stream.flow.videoservice.model.dto.response.VideoInfoResponse;
import stream.flow.videoservice.model.entity.Video;
import stream.flow.videoservice.model.entity.VideoQuality;
import stream.flow.videoservice.model.enums.Quality;
import stream.flow.videoservice.repository.VideoQualityRepository;
import stream.flow.videoservice.repository.VideoRepository;
import stream.flow.videoservice.service.video.VideoQualityService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoQualityServiceImpl implements VideoQualityService {

    private final VideoQualityRepository videoQualityRepository;
    private final VideoRepository videoRepository;
    private final VideoQualityMapper videoQualityMapper;

    @Override
    @Transactional
    public void saveVideoMetadata(VideoInfoResponse videoInfoResponse, String storagePath, UUID videoId, Quality quality) {

        VideoQuality videoQuality = videoQualityMapper.toEntity(videoInfoResponse);
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));

        videoQuality.setVideo(video);
        videoQuality.setQuality(quality);
        videoQuality.setStoragePath(storagePath);

        videoQualityRepository.save(videoQuality);
    }
}
