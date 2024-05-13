package com.example.testing.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define the PasswordEncoder bean directly. This avoids any need for constructor injection for it.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Main security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity in this example
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register","/css/**", "/js/**", "/images/**").permitAll() // Allow unauthenticated access to login and register
                        .anyRequest().authenticated() // Require authentication for any other request
                )
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/login") // Process login requests through POST /login
                        .usernameParameter("username") // Username parameter name in the login form
                        .passwordParameter("password") // Password parameter name in the login form
                        .defaultSuccessUrl("/products/index", true) // Redirect to the home page on successful login
                        .failureUrl("/login?error=true") // Redirect to the login page on failure
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login") // Redirect to login page with logout parameter on successful logout
                );

        return http.build();
    }

    // No need for @Autowired configureGlobal method since we are not using in-memory or custom authentication
    // directly in this configuration class.
}
