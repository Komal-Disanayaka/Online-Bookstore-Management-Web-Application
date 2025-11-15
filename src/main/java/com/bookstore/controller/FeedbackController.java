package com.bookstore.controller;

import com.bookstore.model.Feedback;
import com.bookstore.model.Order;
import com.bookstore.service.FeedbackService;
import com.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private OrderService orderService;
    
    // Show feedback form page
    @GetMapping("/feedback")
    public String showFeedbackPage(@RequestParam Long orderId, Model model) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            
            // Check if order is delivered
            if (!"Delivered".equals(order.getStatus())) {
                model.addAttribute("error", "Can only submit feedback for delivered orders");
                return "redirect:/orders";
            }
            
            // Check if feedback already exists
            if (feedbackService.hasFeedback(orderId)) {
                model.addAttribute("error", "Feedback already submitted for this order");
                return "redirect:/orders";
            }
            
            model.addAttribute("order", order);
            model.addAttribute("title", "Submit Feedback - NovelNest");
            return "feedback";
        }
        return "redirect:/orders";
    }
    
    // Handle feedback form submission
    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam Long orderId,
                                 @RequestParam Integer rating,
                                 @RequestParam String feedbackMessage,
                                 @RequestParam(defaultValue = "false") Boolean deliveryConfirmed,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        try {
            feedbackService.createFeedback(orderId, userDetails.getUsername(), 
                                          rating, feedbackMessage, deliveryConfirmed);
            
            model.addAttribute("message", "✅ Thank you for your feedback!");
            return "redirect:/orders?feedbackSuccess=true";
        } catch (Exception e) {
            model.addAttribute("error", "❌ " + e.getMessage());
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                model.addAttribute("order", orderOpt.get());
            }
            return "feedback";
        }
    }
    
    // API endpoint to check if feedback exists
    @GetMapping("/api/feedback/check/{orderId}")
    @ResponseBody
    public Map<String, Object> checkFeedback(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        response.put("hasFeedback", feedbackService.hasFeedback(orderId));
        return response;
    }
    
    // Show admin feedbacks page
    @GetMapping("/admin/feedbacks")
    public String showAdminFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("totalFeedbacks", feedbacks.size());
        model.addAttribute("verifiedFeedbacks", feedbackService.getVerifiedFeedbacksCount());
        model.addAttribute("averageRating", feedbackService.getAverageRating());
        model.addAttribute("title", "Manage Feedbacks - Admin");
        
        return "admin-feedbacks";
    }
    
    // Delete feedback API endpoint
    @DeleteMapping("/api/feedbacks/delete/{feedbackId}")
    @ResponseBody
    public Map<String, Object> deleteFeedback(@PathVariable Long feedbackId) {
        Map<String, Object> response = new HashMap<>();
        try {
            feedbackService.deleteFeedback(feedbackId);
            response.put("success", true);
            response.put("message", "Feedback deleted successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
