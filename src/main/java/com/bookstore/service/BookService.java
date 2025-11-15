package com.bookstore.service;

import com.bookstore.dto.*;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    private static final String UPLOAD_DIR = "uploads/books/";
    
    /**
     * Add a new book with image upload
     */
    @Transactional
    public BookResponseDto addBook(BookDto bookDto, MultipartFile image) {
        // Check if book ID already exists
        if (bookRepository.existsByBookId(bookDto.getBookId())) {
            throw new RuntimeException("Book ID already exists");
        }
        
        // Create new book
        Book book = new Book();
        book.setBookId(bookDto.getBookId());
        book.setBookName(bookDto.getBookName());
        book.setPrice(bookDto.getPrice());
        book.setDescription(bookDto.getDescription());
        book.setAuthorName(bookDto.getAuthorName());
        book.setAvailable(true);
        
        // Handle image upload
        if (image != null && !image.isEmpty()) {
            String imagePath = saveImage(image);
            book.setImagePath(imagePath);
        }
        
        Book savedBook = bookRepository.save(book);
        return convertToResponseDto(savedBook);
    }
    
    /**
     * Get book by ID
     */
    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return convertToResponseDto(book);
    }
    
    /**
     * Get book by book ID
     */
    public BookResponseDto getBookByBookId(String bookId) {
        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with bookId: " + bookId));
        return convertToResponseDto(book);
    }
    
    /**
     * Get all books
     */
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all available books
     */
    public List<BookResponseDto> getAvailableBooks() {
        return bookRepository.findByAvailableTrue().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update book (cannot update bookId and bookName)
     */
    @Transactional
    public BookResponseDto updateBook(Long id, BookUpdateDto updateDto, MultipartFile image) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        // Update fields (bookId and bookName are NOT updated)
        book.setPrice(updateDto.getPrice());
        book.setDescription(updateDto.getDescription());
        book.setAuthorName(updateDto.getAuthorName());
        
        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (book.getImagePath() != null) {
                deleteImage(book.getImagePath());
            }
            String imagePath = saveImage(image);
            book.setImagePath(imagePath);
        }
        
        Book updatedBook = bookRepository.save(book);
        return convertToResponseDto(updatedBook);
    }
    
    /**
     * Delete book
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        // Delete image if exists
        if (book.getImagePath() != null) {
            deleteImage(book.getImagePath());
        }
        
        bookRepository.delete(book);
    }
    
    /**
     * Search books by name
     */
    public List<BookResponseDto> searchBooksByName(String name) {
        return bookRepository.findByBookNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search books by author
     */
    public List<BookResponseDto> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(author).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search and sort books
     * @param search - search keyword for book name
     * @param sort - sorting option: "price_asc" or "price_desc"
     */
    public List<BookResponseDto> searchAndSortBooks(String search, String sort) {
        List<Book> books;
        
        if (search != null && !search.trim().isEmpty()) {
            // Search with sorting
            if ("price_asc".equals(sort)) {
                books = bookRepository.findByBookNameContainingIgnoreCaseOrderByPriceAsc(search);
            } else if ("price_desc".equals(sort)) {
                books = bookRepository.findByBookNameContainingIgnoreCaseOrderByPriceDesc(search);
            } else {
                books = bookRepository.findByBookNameContainingIgnoreCase(search);
            }
        } else {
            // Just sorting
            if ("price_asc".equals(sort)) {
                books = bookRepository.findAllByOrderByPriceAsc();
            } else if ("price_desc".equals(sort)) {
                books = bookRepository.findAllByOrderByPriceDesc();
            } else {
                books = bookRepository.findAll();
            }
        }
        
        return books.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Save uploaded image
     */
    private String saveImage(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String filename = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return UPLOAD_DIR + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }
    
    /**
     * Delete image file
     */
    private void deleteImage(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete image: " + e.getMessage());
        }
    }
    
    /**
     * Convert Book entity to BookResponseDto
     */
    private BookResponseDto convertToResponseDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(book.getId());
        dto.setBookId(book.getBookId());
        dto.setBookName(book.getBookName());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setAuthorName(book.getAuthorName());
        dto.setImagePath(book.getImagePath());
        dto.setAvailable(book.getAvailable());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        return dto;
    }
}
