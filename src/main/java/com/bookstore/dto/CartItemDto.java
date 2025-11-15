package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private Long bookId;
    private String bookName;
    private String authorName;
    private String imagePath;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
