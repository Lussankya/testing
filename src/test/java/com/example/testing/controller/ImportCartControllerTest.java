package com.example.testing.controller;

import com.example.testing.model.ImportCart;
import com.example.testing.repo.ImportCartRepository;
import com.example.testing.service.ImportCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImportCartController.class)
public class ImportCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportCartService importCartService;

    @MockBean
    private ImportCartRepository importCartRepository;
    @Autowired
    private WebApplicationContext context;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void testAddToCartWithEmptyFields() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "")
                        .param("price", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "quantity", "price"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithNonNumberQuantity() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "abc")
                        .param("price", "1111"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "quantity"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithFloatQuantity() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "1.5")
                        .param("price", "1111"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "quantity"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithEmptyQuantity() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "")
                        .param("price", "1111"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "quantity"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithNegativeQuantity() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "-5")
                        .param("price", "1111"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "quantity"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithNonNumberPrice() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "20")
                        .param("price", "abcd"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "price"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithFloatPrice() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "20")
                        .param("price", "12.5"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "price"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithEmptyPrice() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "20")
                        .param("price", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "price"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartWithNegativePrice() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "20")
                        .param("price", "-100"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("importCart", "price"))
                .andExpect(view().name("view-cart"));
    }

    @Test
    void testAddToCartSuccessfully() throws Exception {
        doNothing().when(importCartService).addToCart(1L, 1L, 20, 100000);

        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("supplierId", "1")
                        .param("quantity", "20")
                        .param("price", "100000"))
                .andExpect(status().isOk());

    }

    @Test
    void testRemoveItemFromCartSuccessfully() throws Exception {
        doNothing().when(importCartRepository).deleteById(1L);

        mockMvc.perform(post("/cart/remove/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/view"));
    }

    @Test
    void testRemoveItemFromCartWithNonExistentId() throws Exception {
        doThrow(new IllegalArgumentException("Invalid cart item ID: 999")).when(importCartRepository).deleteById(999L);

        mockMvc.perform(post("/cart/remove/999"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("error"));
    }
}
