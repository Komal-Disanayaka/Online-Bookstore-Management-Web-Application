package com.bookstore.service;

import com.bookstore.dto.CartItemDto;
import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    
    /**
     * Add item to cart
     */
    @Transactional
    public CartItemDto addToCart(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (!book.getAvailable()) {
            throw new RuntimeException("Book is not available");
        }
        
        // Check if item already exists in cart
        var existingItem = cartItemRepository.findByUserAndBookId(user, bookId);
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            CartItem updated = cartItemRepository.save(cartItem);
            return convertToDto(updated);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setBook(book);
            cartItem.setQuantity(1);
            cartItem.setPriceAtAddition(book.getPrice());
            CartItem saved = cartItemRepository.save(cartItem);
            return convertToDto(saved);
        }
    }
    
    /**
     * Get user's cart items
     */
    public List<CartItemDto> getCartItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartItemRepository.findByUserOrderByAddedAtDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update cart item quantity
     */
    @Transactional
    public CartItemDto updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        
        cartItem.setQuantity(quantity);
        CartItem updated = cartItemRepository.save(cartItem);
        return convertToDto(updated);
    }
    
    /**
     * Remove item from cart
     */
    @Transactional
    public void removeFromCart(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new RuntimeException("Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }
    
    /**
     * Clear user's cart
     */
    @Transactional
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        cartItemRepository.deleteByUser(user);
    }
    
    /**
     * Get cart item count
     */
    public long getCartItemCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartItemRepository.countByUser(user);
    }
    
    /**
     * Calculate cart total
     */
    public BigDecimal getCartTotal(String username) {
        List<CartItemDto> items = getCartItems(username);
        return items.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Convert CartItem entity to DTO
     */
    private CartItemDto convertToDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setBookId(cartItem.getBook().getId());
        dto.setBookName(cartItem.getBook().getBookName());
        dto.setAuthorName(cartItem.getBook().getAuthorName());
        dto.setImagePath(cartItem.getBook().getImagePath());
        dto.setPrice(cartItem.getPriceAtAddition());
        dto.setQuantity(cartItem.getQuantity());
        dto.setSubtotal(cartItem.getSubtotal());
        return dto;
    }
}
