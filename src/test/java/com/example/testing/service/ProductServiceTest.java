package com.example.testing.service;

import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.repo.CategoryRepository;
import com.example.testing.repo.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductWhenProductExists() {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "Smartphone", "Latest model", category);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByNameAndCategory("Smartphone", category)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(product));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testAddProductWhenProductDoesNotExist() {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "Smartphone", "Latest model", category);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByNameAndCategory("Smartphone", category)).thenReturn(false);

        productService.addProduct(product);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testAddProductWithMissingCategory() {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "Smartphone", "Latest model", category);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(product));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testAddProductWithNullCategory() {
        Product product = new Product(null, "Smartphone", "Latest model", null);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(product));
        verify(productRepository, never()).save(any(Product.class));
    }
}
