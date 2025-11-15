package com.bookstore.controller;

import com.bookstore.dto.*;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    
    /**
     * Add a new book (Admin only)
     * POST /api/books
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> addBook(
            @RequestParam("bookId") String bookId,
            @RequestParam("bookName") String bookName,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("authorName") String authorName,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            BookDto bookDto = new BookDto();
            bookDto.setBookId(bookId);
            bookDto.setBookName(bookName);
            bookDto.setPrice(price);
            bookDto.setDescription(description);
            bookDto.setAuthorName(authorName);
            
            BookResponseDto book = bookService.addBook(bookDto, image);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Book added successfully");
            response.put("book", book);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get book by ID
     * GET /api/books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            BookResponseDto book = bookService.getBookById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("book", book);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get all books with optional search and sort
     * GET /api/books?search=keyword&sort=price_asc|price_desc
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        try {
            List<BookResponseDto> books = bookService.searchAndSortBooks(search, sort);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", books.size());
            response.put("books", books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get available books only
     * GET /api/books/available
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBooks() {
        try {
            List<BookResponseDto> books = bookService.getAvailableBooks();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", books.size());
            response.put("books", books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update book (cannot update bookId and bookName) (Admin only)
     * PUT /api/books/{id}
     */
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateBook(
            @PathVariable Long id,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("authorName") String authorName,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            BookUpdateDto updateDto = new BookUpdateDto();
            updateDto.setPrice(price);
            updateDto.setDescription(description);
            updateDto.setAuthorName(authorName);
            
            BookResponseDto book = bookService.updateBook(id, updateDto, image);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Book updated successfully");
            response.put("book", book);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Delete book (Admin only)
     * DELETE /api/books/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Book deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Search books by name
     * GET /api/books/search/name/{name}
     */
    @GetMapping("/search/name/{name}")
    public ResponseEntity<?> searchBooksByName(@PathVariable String name) {
        try {
            List<BookResponseDto> books = bookService.searchBooksByName(name);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", books.size());
            response.put("books", books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Search books by author
     * GET /api/books/search/author/{author}
     */
    @GetMapping("/search/author/{author}")
    public ResponseEntity<?> searchBooksByAuthor(@PathVariable String author) {
        try {
            List<BookResponseDto> books = bookService.searchBooksByAuthor(author);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", books.size());
            response.put("books", books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
