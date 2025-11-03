package stream.flow.videoservice.exception.category;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(UUID categoryId) {
        super("Category not found with id: " + categoryId);
    }
}


