package com.example.testing.controller;

import com.example.testing.model.User;
import com.example.testing.repo.UserRepository;
import com.example.testing.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Ensure transactional behavior for database rollback after each test
public class LoginControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testLoginWithCorrectCredentials() throws Exception {
        // Prepare test data
        User user = new User();
        user.setUsername("root2");
        user.setPassword("$2a$10$O1mVi2dNe4bLeaK2KXH4hedJ0nFasTUwMT5E7GHkrpu/FJRHzK1my"); // Password encoded with BCrypt
        userRepository.save(user);

        mockMvc.perform(post("/login")
                        .param("username", "root2")
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/index"));
    }

    @Test
    public void testLoginWithIncorrectCredentials() throws Exception {
        // Prepare test data
        User user = new User();
        user.setUsername("root");
        user.setPassword("$2a$10$12tpz8RNFqVrFyoQPO7yDuKwSJpFt9OpbG7eVcl5CYC10N9VWKI5u"); // Password encoded with BCrypt
        userRepository.save(user);

        mockMvc.perform(post("/login")
                        .param("username", "root")
                        .param("password", "1fsdfe")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    public void testAccessProtectedResourceWithoutAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/products/index"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
