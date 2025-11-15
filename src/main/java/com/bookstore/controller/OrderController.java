package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.service.OrderService;
import com.bookstore.service.OrderInquiryService;
import com.bookstore.repository.UserRepository;
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
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderInquiryService orderInquiryService;
    
    // Show checkout page
    @GetMapping("/checkout")
    public String showCheckout(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("title", "Checkout - NovelNest");
        return "checkout";
    }
    
    // Process checkout and create order
    @PostMapping("/api/orders/create")
    @ResponseBody
    public Map<String, Object> createOrder(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam String deliveryAddress,
                                          @RequestParam String deliveryCity,
                                          @RequestParam String deliveryPostalCode,
                                          @RequestParam String deliveryPhone,
                                          @RequestParam String cardNumber,
                                          @RequestParam String cardHolderName) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.createOrder(
                userDetails.getUsername(),
                deliveryAddress,
                deliveryCity,
                deliveryPostalCode,
                deliveryPhone,
                cardNumber,
                cardHolderName
            );
            response.put("success", true);
            response.put("message", "Order placed successfully!");
            response.put("orderId", order.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // Show user orders page
    @GetMapping("/orders")
    public String showUserOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderService.getUserOrders(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("title", "My Orders - NovelNest");
        return "user-orders";
    }
    
    // Show admin order management page
    @GetMapping("/admin/orders")
    public String showAdminOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("title", "Order Management - Admin");
        return "admin-orders";
    }
    
    // Update order status (admin only)
    @PutMapping("/api/orders/{orderId}/status")
    @ResponseBody
    public Map<String, Object> updateOrderStatus(@PathVariable Long orderId,
                                                 @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.updateOrderStatus(orderId, status);
            response.put("success", true);
            response.put("message", "Order status updated successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // Get order details
    @GetMapping("/api/orders/{orderId}")
    @ResponseBody
    public Map<String, Object> getOrderDetails(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                response.put("success", true);
                response.put("order", orderOpt.get());
            } else {
                response.put("success", false);
                response.put("message", "Order not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // Show inquiry form page
    @GetMapping("/inquiry")
    public String showInquiryPage(@RequestParam Long orderId, Model model) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
            model.addAttribute("previousInquiries", orderInquiryService.getInquiriesByOrder(orderId));
            model.addAttribute("title", "Send Inquiry - NovelNest");
            return "inquiry";
        }
        return "redirect:/orders";
    }
    
    // Handle inquiry form submission
    @PostMapping("/inquiry")
    public String submitInquiry(@RequestParam Long orderId,
                               @RequestParam String inquiryMessage,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        try {
            // Create the inquiry using the service
            orderInquiryService.createInquiry(orderId, userDetails.getUsername(), inquiryMessage);
            
            model.addAttribute("message", "✅ Your inquiry has been sent successfully! We'll respond soon.");
            
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                model.addAttribute("order", orderOpt.get());
            }
            model.addAttribute("previousInquiries", orderInquiryService.getInquiriesByOrder(orderId));
            
            return "inquiry";
        } catch (Exception e) {
            model.addAttribute("error", "❌ Failed to send inquiry: " + e.getMessage());
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                model.addAttribute("order", orderOpt.get());
            }
            model.addAttribute("previousInquiries", orderInquiryService.getInquiriesByOrder(orderId));
            return "inquiry";
        }
    }
}
