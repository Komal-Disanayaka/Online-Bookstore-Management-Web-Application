package com.bookstore.repository;

import com.bookstore.model.Feedback;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByUserOrderByFeedbackDateDesc(User user);
    
    Optional<Feedback> findByOrder(Order order);
    
    List<Feedback> findAllByOrderByFeedbackDateDesc();
    
    boolean existsByOrder(Order order);
}
