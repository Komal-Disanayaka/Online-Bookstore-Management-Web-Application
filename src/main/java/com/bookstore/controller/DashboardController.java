package com.bookstore.controller;

import com.bookstore.service.UserService;
import com.bookstore.service.BookService;
import com.bookstore.service.CartService;
import com.bookstore.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final UserService userService;
    private final BookService bookService;
    private final CartService cartService;
    private final FeedbackService feedbackService;
    
    /**
     * Public Dashboard - Before Login
     * Accessible at: /
     */
    @GetMapping("/")
    public String publicDashboard(Model model) {
        model.addAttribute("title", "Welcome to Online Bookstore");
        model.addAttribute("pageType", "public");
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "public-dashboard";
    }
    
    /**
     * Login Page
     * Accessible at: /login
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Login - Online Bookstore");
        return "login";
    }
    
    /**
     * Registration Page
     * Accessible at: /register
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Register - Online Bookstore");
        return "register";
    }
    
    /**
     * User Dashboard - After Login
     * Accessible at: /dashboard
     */
    @GetMapping("/dashboard")
    public String userDashboard(Authentication authentication, Model model,
                               @org.springframework.web.bind.annotation.RequestParam(required = false) String search,
                               @org.springframework.web.bind.annotation.RequestParam(required = false) String sort) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                var user = userService.getUserByUsername(username);
                var books = bookService.searchAndSortBooks(search, sort);
                model.addAttribute("user", user);
                model.addAttribute("books", books);
                model.addAttribute("search", search != null ? search : "");
                model.addAttribute("sort", sort != null ? sort : "");
                model.addAttribute("title", "Dashboard - " + user.getFullName());
                model.addAttribute("pageType", "private");
            } catch (Exception e) {
                model.addAttribute("error", "User not found");
            }
        }
        return "user-dashboard";
    }
    
    /**
     * User Profile Page
     * Accessible at: /profile
     */
    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                var user = userService.getUserByUsername(username);
                model.addAttribute("user", user);
                model.addAttribute("title", "Profile - " + user.getFullName());
            } catch (Exception e) {
                model.addAttribute("error", "User not found");
            }
        }
        return "profile";
    }
    
    /**
     * Admin Dashboard
     * Accessible at: /admin/dashboard
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model,
                                @org.springframework.web.bind.annotation.RequestParam(required = false) String search,
                                @org.springframework.web.bind.annotation.RequestParam(required = false) String sort) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                var user = userService.getUserByUsername(username);
                var allUsers = userService.getAllUsers();
                var allBooks = bookService.searchAndSortBooks(search, sort);
                model.addAttribute("user", user);
                model.addAttribute("allUsers", allUsers);
                model.addAttribute("allBooks", allBooks);
                model.addAttribute("search", search != null ? search : "");
                model.addAttribute("sort", sort != null ? sort : "");
                model.addAttribute("userCount", allUsers.size());
                model.addAttribute("bookCount", allBooks.size());
                model.addAttribute("title", "Admin Dashboard");
                model.addAttribute("pageType", "admin");
            } catch (Exception e) {
                model.addAttribute("error", "Error loading admin dashboard");
            }
        }
        return "admin-dashboard";
    }
    
    /**
     * Cart Page
     * Accessible at: /cart
     */
    @GetMapping("/cart")
    public String cartPage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                var user = userService.getUserByUsername(username);
                var cartItems = cartService.getCartItems(username);
                var cartTotal = cartService.getCartTotal(username);
                model.addAttribute("user", user);
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("cartTotal", cartTotal);
                model.addAttribute("title", "Shopping Cart");
            } catch (Exception e) {
                model.addAttribute("error", "Error loading cart");
            }
        }
        return "cart";
    }
}
