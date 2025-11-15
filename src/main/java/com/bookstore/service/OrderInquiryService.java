package com.bookstore.service;

import com.bookstore.model.*;
import com.bookstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderInquiryService {
    
    @Autowired
    private OrderInquiryRepository orderInquiryRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public OrderInquiry createInquiry(Long orderId, String username, String inquiryMessage) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found");
        }
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        
        OrderInquiry inquiry = new OrderInquiry();
        inquiry.setOrder(orderOpt.get());
        inquiry.setUser(userOpt.get());
        inquiry.setInquiryMessage(inquiryMessage);
        
        return orderInquiryRepository.save(inquiry);
    }
    
    public List<OrderInquiry> getInquiriesByOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            return orderInquiryRepository.findByOrderOrderByInquiryDateDesc(orderOpt.get());
        }
        return List.of();
    }
    
    public List<OrderInquiry> getAllInquiries() {
        return orderInquiryRepository.findAllByOrderByInquiryDateDesc();
    }
    
    public List<OrderInquiry> getPendingInquiries() {
        return orderInquiryRepository.findByStatusOrderByInquiryDateDesc("Pending");
    }
    
    @Transactional
    public void replyToInquiry(Long inquiryId, String adminReply) {
        Optional<OrderInquiry> inquiryOpt = orderInquiryRepository.findById(inquiryId);
        if (inquiryOpt.isPresent()) {
            OrderInquiry inquiry = inquiryOpt.get();
            inquiry.setAdminReply(adminReply);
            inquiry.setStatus("Replied");
            inquiry.setReplyDate(LocalDateTime.now());
            orderInquiryRepository.save(inquiry);
        }
    }
    
    public Optional<OrderInquiry> getInquiryById(Long inquiryId) {
        return orderInquiryRepository.findById(inquiryId);
    }
}
