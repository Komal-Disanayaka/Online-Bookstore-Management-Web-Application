package com.bookstore.repository;

import com.bookstore.model.Order;
import com.bookstore.model.OrderInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderInquiryRepository extends JpaRepository<OrderInquiry, Long> {
    List<OrderInquiry> findByOrderOrderByInquiryDateDesc(Order order);
    List<OrderInquiry> findAllByOrderByInquiryDateDesc();
    List<OrderInquiry> findByStatusOrderByInquiryDateDesc(String status);
}
