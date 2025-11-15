package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    
    @NotBlank(message = "Book ID is required")
    @Size(min = 3, max = 50, message = "Book ID must be between 3 and 50 characters")
    private String bookId;
    
    @NotBlank(message = "Book name is required")
    @Size(min = 2, max = 200, message = "Book name must be between 2 and 200 characters")
    private String bookName;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @NotBlank(message = "Author name is required")
    @Size(min = 2, max = 100, message = "Author name must be between 2 and 100 characters")
    private String authorName;
}
