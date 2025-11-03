package stream.flow.videoservice.service.storage.impl;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stream.flow.videoservice.exception.file.FileUploadException;
import stream.flow.videoservice.service.storage.StorageService;
import stream.flow.videoservice.service.validation.FileUtilService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final FileUtilService fileUtilService;

    @Value("${minio.url-expiry:3600}")
    private int defaultUrlExpiry;

    @Override
    public String uploadFile(MultipartFile file, String bucketName, String fileName) {
        try {
            createBucketIfNotExists(bucketName);

            String contentType = fileUtilService.getContentType(file.getOriginalFilename());
            
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(contentType)
                        .build()
                );
            }

            log.info("File uploaded successfully to MinIO: bucket={}, file={}", bucketName, fileName);
            
            // Возвращаем постоянный путь к файлу
            return String.format("%s/%s", bucketName, fileName);
            
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

