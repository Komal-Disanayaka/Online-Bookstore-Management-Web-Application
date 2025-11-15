package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByBookId(String bookId);
    
    boolean existsByBookId(String bookId);
    
    List<Book> findByAvailableTrue();
    
    List<Book> findByBookNameContainingIgnoreCase(String bookName);
    
    List<Book> findByAuthorNameContainingIgnoreCase(String authorName);
    
    List<Book> findAllByOrderByPriceAsc();
    
    List<Book> findAllByOrderByPriceDesc();
    
    List<Book> findByBookNameContainingIgnoreCaseOrderByPriceAsc(String bookName);
    
    List<Book> findByBookNameContainingIgnoreCaseOrderByPriceDesc(String bookName);
}
