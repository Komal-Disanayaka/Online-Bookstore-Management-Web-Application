package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {
    
    private Long id;
    private String bookId;
    private String bookName;
    private BigDecimal price;
    private String description;
    private String authorName;
    private String imagePath;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
