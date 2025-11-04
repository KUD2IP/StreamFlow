package stream.flow.videoservice.service.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Абстракция для работы с файловым хранилищем
 * Позволяет легко переключаться между разными провайдерами (MinIO, S3, Azure, etc.)
 */
public interface StorageService {

    /**
     * Загружает файл в хранилище
     * 
     * @param path - путь до файла
     * @return URL загруженного файла
     */
    String uploadFile(String path, String bucketName);

    /**
     * Проверяет существование bucket
     * 
     * @param bucketName имя bucket
     * @return true если bucket существует
     */
    boolean bucketExists(String bucketName);

    /**
     * Создает bucket если не существует
     * 
     * @param bucketName имя bucket
     */
    void createBucketIfNotExists(String bucketName);
}




