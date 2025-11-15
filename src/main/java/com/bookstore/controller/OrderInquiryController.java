package com.bookstore.controller;

import com.bookstore.model.OrderInquiry;
import com.bookstore.service.OrderInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderInquiryController {
    
    @Autowired
    private OrderInquiryService orderInquiryService;
    
    // Create inquiry (User)
    @PostMapping("/api/inquiries/create")
    @ResponseBody
    public Map<String, Object> createInquiry(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam Long orderId,
                                            @RequestParam String inquiryMessage) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderInquiry inquiry = orderInquiryService.createInquiry(
                orderId,
                userDetails.getUsername(),
                inquiryMessage
            );
            response.put("success", true);
            response.put("message", "Inquiry sent successfully!");
            response.put("inquiryId", inquiry.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // Get inquiries for an order
    @GetMapping("/api/inquiries/order/{orderId}")
    @ResponseBody
    public Map<String, Object> getOrderInquiries(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<OrderInquiry> inquiries = orderInquiryService.getInquiriesByOrder(orderId);
            response.put("success", true);
            response.put("inquiries", inquiries);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // Reply to inquiry (Admin)
    @PostMapping("/api/inquiries/reply")
    @ResponseBody
    public Map<String, Object> replyToInquiry(@RequestParam Long inquiryId,
                                             @RequestParam String adminReply) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderInquiryService.replyToInquiry(inquiryId, adminReply);
            response.put("success", true);
            response.put("message", "Reply sent successfully!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // Get all inquiries (Admin)
    @GetMapping("/api/inquiries/all")
    @ResponseBody
    public Map<String, Object> getAllInquiries() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<OrderInquiry> inquiries = orderInquiryService.getAllInquiries();
            response.put("success", true);
            response.put("inquiries", inquiries);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // Show admin inquiries page
    @GetMapping("/admin/inquiries")
    public String showAdminInquiries(Model model) {
        List<OrderInquiry> inquiries = orderInquiryService.getAllInquiries();
        
        long pendingCount = inquiries.stream()
            .filter(i -> "Pending".equals(i.getStatus()))
            .count();
        long repliedCount = inquiries.stream()
            .filter(i -> "Replied".equals(i.getStatus()))
            .count();
        
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("repliedCount", repliedCount);
        model.addAttribute("totalCount", inquiries.size());
        model.addAttribute("title", "Manage Inquiries - Admin");
        
        return "admin-inquiries";
    }
}
