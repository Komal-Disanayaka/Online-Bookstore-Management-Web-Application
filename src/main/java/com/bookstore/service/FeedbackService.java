package com.bookstore.service;

import com.bookstore.model.Feedback;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.repository.FeedbackRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public Feedback createFeedback(Long orderId, String username, Integer rating, 
                                   String feedbackMessage, Boolean deliveryConfirmed) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found");
        }
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        
        Order order = orderOpt.get();
        
        // Check if feedback already exists for this order
        if (feedbackRepository.existsByOrder(order)) {
            throw new RuntimeException("Feedback already submitted for this order");
        }
        
        // Verify order is delivered
        if (!"Delivered".equals(order.getStatus())) {
            throw new RuntimeException("Can only submit feedback for delivered orders");
        }
        
        Feedback feedback = new Feedback();
        feedback.setOrder(order);
        feedback.setUser(userOpt.get());
        feedback.setRating(rating);
        feedback.setFeedbackMessage(feedbackMessage);
        feedback.setDeliveryConfirmed(deliveryConfirmed);
        
        return feedbackRepository.save(feedback);
    }
    
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAllByOrderByFeedbackDateDesc();
    }
    
    public List<Feedback> getUserFeedbacks(User user) {
        return feedbackRepository.findByUserOrderByFeedbackDateDesc(user);
    }
    
    public Optional<Feedback> getFeedbackByOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            return feedbackRepository.findByOrder(orderOpt.get());
        }
        return Optional.empty();
    }
    
    public boolean hasFeedback(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        return orderOpt.isPresent() && feedbackRepository.existsByOrder(orderOpt.get());
    }
    
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (!feedbackOpt.isPresent()) {
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepository.deleteById(feedbackId);
    }
    
    public Double getAverageRating() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        if (feedbacks.isEmpty()) {
            return 0.0;
        }
        return feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }
    
    public long getVerifiedFeedbacksCount() {
        return feedbackRepository.findAll().stream()
                .filter(Feedback::getDeliveryConfirmed)
                .count();
    }
}
