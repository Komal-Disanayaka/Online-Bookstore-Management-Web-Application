package com.bookstore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(nullable = false, length = 1000)
    private String feedbackMessage;
    
    @Column(nullable = false)
    private LocalDateTime feedbackDate;
    
    @Column(nullable = false)
    private Boolean deliveryConfirmed = false;
    
    // Constructors
    public Feedback() {
        this.feedbackDate = LocalDateTime.now();
    }
    
    public Feedback(User user, Order order, Integer rating, String feedbackMessage, Boolean deliveryConfirmed) {
        this.user = user;
        this.order = order;
        this.rating = rating;
        this.feedbackMessage = feedbackMessage;
        this.deliveryConfirmed = deliveryConfirmed;
        this.feedbackDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getFeedbackMessage() {
        return feedbackMessage;
    }
    
    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }
    
    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }
    
    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
    
    public Boolean getDeliveryConfirmed() {
        return deliveryConfirmed;
    }
    
    public void setDeliveryConfirmed(Boolean deliveryConfirmed) {
        this.deliveryConfirmed = deliveryConfirmed;
    }
}
