package com.bookstore.repository;

import com.bookstore.model.CartItem;
import com.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByUser(User user);
    
    List<CartItem> findByUserOrderByAddedAtDesc(User user);
    
    Optional<CartItem> findByUserAndBookId(User user, Long bookId);
    
    void deleteByUser(User user);
    
    long countByUser(User user);
}
