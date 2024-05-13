package com.example.testing.controller;

import com.example.testing.service.UserService;
import com.example.testing.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Controller // Use @Controller for HTML views
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Assuming you have a register.html in the src/main/resources/templates directory
    }
    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userService.createUser(user);
            return "redirect:/login";
        } catch (Exception e) {
            // Log the exception details as needed
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
            return "register";
        }
    }
    // Serve the login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Create a new user (API)
    @PostMapping("/user/")
    @ResponseBody // Indicate that the return type should be bound to the web response body
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Get a user by ID (API)
    @GetMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all users (API)
    @GetMapping("/user/")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Update a user (API)
    @PutMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a user (API)
    @DeleteMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}