package stream.flow.videoservice.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    /**
     * Конфигурация для загрузки файлов
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Максимальный размер файла: 10GB
        factory.setMaxFileSize(DataSize.ofGigabytes(10));
        
        // Максимальный размер запроса: 10GB
        factory.setMaxRequestSize(DataSize.ofGigabytes(10));
        
        // Размер буфера для загрузки: 1MB
        factory.setFileSizeThreshold(DataSize.ofMegabytes(1));
        
        return factory.createMultipartConfig();
    }

    /**
     * Multipart resolver для обработки файлов
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}

