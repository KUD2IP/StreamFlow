package stream.flow.videoservice.service.storage.impl;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stream.flow.videoservice.exception.file.FileUploadException;
import stream.flow.videoservice.service.storage.StorageService;
import stream.flow.videoservice.service.file.FileService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final FileService fileService;

    @Value("${video.temp-dir}")
    private String tempDir;

    @Override
    public String uploadFile(String path, String bucketName) {
        try {
            createBucketIfNotExists(bucketName);

            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("Input file not found: " + path);
            }

            // Например: "./video-temp/e832d10e/p720.mp4" -> "e832d10e/p720.mp4"
            Path tempDirPath = Paths.get(tempDir);
            String relativePath = tempDirPath.relativize(filePath).toString();

            String objectName = filePath.getFileName().toString();

            // Определяем contentType
            String extension = fileService.getFileExtension(objectName);
            String contentType = "video/" + extension;

            long fileSize = Files.size(filePath);

            try (InputStream inputStream = Files.newInputStream(filePath)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(relativePath)
                                .stream(inputStream, fileSize, -1)
                                .contentType(contentType)
                                .build()
                );
            }

            log.info("File uploaded successfully to MinIO: bucket={}, object={}", bucketName, relativePath);

            return String.format("%s/%s", bucketName, relativePath);

        } catch (Exception e) {
            log.error("Failed to upload file to MinIO: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to upload file to storage", e);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to check bucket existence: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void createBucketIfNotExists(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                );
                log.info("Bucket created: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to create bucket: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to create bucket", e);
        }
    }
}

