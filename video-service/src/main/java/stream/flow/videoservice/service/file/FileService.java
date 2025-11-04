package stream.flow.videoservice.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class FileService {

    /**
     * Извлекает расширение файла из имени
     */
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("Filename doesn't end with extension");
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}




