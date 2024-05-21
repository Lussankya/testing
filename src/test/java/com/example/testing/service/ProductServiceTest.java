package com.example.testing.service;

import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.repo.CategoryRepository;
import com.example.testing.repo.ImportCartRepository;
import com.example.testing.repo.ImportDetailRepository;
import com.example.testing.repo.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;


    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;
    @Mock
    ImportDetailRepository importDetailRepository;
    @Mock
    ImportCartRepository importCartRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductWithAllFieldsEmpty() {
        Category category = null;
        Product product = new Product(null, "", "", category);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });

        assertEquals("Category is required", exception.getMessage());
    }

    @Test
    void testAddProductSuccessfully() {
        Category category = new Category(1L, "Book");
        Product product = new Product(1L, "sua", "sua", category);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByNameAndCategory(any(String.class), any(Category.class))).thenReturn(false);

        productService.addProduct(product);

        assertNotNull(product.getId());
        assertEquals("sua", product.getName());
        assertEquals("sua", product.getDescription());
    }

    @Test
    void testAddProductWithMissingName() {
        Category category = new Category(1L, "Book");
        Product product = new Product(null, "", "sua", category);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });

        assertEquals("Product name is required", exception.getMessage());
    }

    @Test
    void testAddProductWithDuplicateNameInSameCategory() {
        Category category = new Category(1L, "Book");
        Product product = new Product(null, "sua", "sua2", category);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByNameAndCategory(any(String.class), any(Category.class))).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });

        assertEquals("A product with the same name already exists in the given category.", exception.getMessage());
    }

    @Test
    void testAddProductWithDuplicateNameInDifferentCategory() {
        Category categoryBook = new Category(1L, "Book");
        Category categoryConsole = new Category(2L, "Console");
        Product product = new Product(null, "sua", "sua2", categoryConsole);

        when(categoryService.getCategoryById(2L)).thenReturn(Optional.of(categoryConsole));
        when(productRepository.existsByNameAndCategory(any(String.class), any(Category.class))).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1L);
            return savedProduct;
        });

        productService.addProduct(product);

        assertNotNull(product.getId());
        assertEquals("sua", product.getName());
        assertEquals("sua2", product.getDescription());
    }


    @Test
    void testAddProductWithMissingDescription() {
        Category category = new Category(1L, "Console");
        Product product = new Product(null, "PS4", "", category);

        when(categoryService.getCategoryById(any(Long.class))).thenReturn(Optional.of(category));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });

        assertEquals("Description is required", exception.getMessage());
    }


    @Test
    void testAddProductWithNameExceedingMaxLength() {
        Category category = new Category(1L, "Console");
        String longName = "a".repeat(256); // Assuming max length is 255
        Product product = new Product(null, longName, "dien thoai", category);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });

        assertEquals("Product name must not exceed 255 characters", exception.getMessage());
    }

    @Test
    void testSearchProductWithEmptySearchBox() {
        Product product1 = new Product(1L, "Product1", "Description1", new Category(1L, "Category1"));
        Product product2 = new Product(2L, "Product2", "Description2", new Category(2L, "Category2"));

        when(productRepository.findByNameContainingIgnoreCase("")).thenReturn(Arrays.asList(product1, product2));

        assertDoesNotThrow(() -> {
            productService.searchProductsByName("");
        });

        assertEquals(2, productService.searchProductsByName("").size());
    }

    @Test
    void testSearchProductWithValidInput() {
        Product product1 = new Product(1L, "Tablet", "Description1", new Category(1L, "Electronics"));
        Product product2 = new Product(2L, "Toy", "Description2", new Category(2L, "Kids"));

        when(productRepository.findByNameContainingIgnoreCase("T")).thenReturn(Arrays.asList(product1, product2));

        assertDoesNotThrow(() -> {
            productService.searchProductsByName("T");
        });

        assertEquals(2, productService.searchProductsByName("T").size());
    }

    @Test
    void testSearchProductWithNonExistentProduct() {
        when(productRepository.findByNameContainingIgnoreCase("basketballs")).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> {
            productService.searchProductsByName("basketballs");
        });

        assertEquals(0, productService.searchProductsByName("basketballs").size());
    }

    @Test
    void testEditProductWithMissingName() {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(updatedProduct);
        });

        assertEquals("Product name is required", exception.getMessage());
    }

    @Test
    void testEditProductSuccessfully() {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "iphone", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndCategory(any(String.class), any(Category.class))).thenReturn(false);
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));

        productService.addProduct(updatedProduct);

        assertEquals("iphone", updatedProduct.getName());
        assertEquals("A high-end smartphone with advanced features", updatedProduct.getDescription());
    }

    @Test
    void testEditProductWithDuplicateNameAndCategory() {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(2L, "Galaxy S3", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryService.getCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(productRepository.existsByNameAndCategory(updatedProduct.getName(), category)).thenReturn(true);

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            productService.addProduct(updatedProduct);
        });

        assertEquals("A product with the same name already exists in the given category.", exception.getMessage());
    }


    @Test
    void testEditProductWithMissingCategory() {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "Galaxy S3", "A high-end smartphone with advanced features", null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(updatedProduct);
        });

        assertEquals("Category is required", exception.getMessage());
    }

    @Test
    void testEditProductWithMissingDescription() {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "Galaxy S3", "", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(updatedProduct);
        });

        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    void testEditProductWithNameExceedingMaxLength() {
        Category category = new Category(1L, "Electronics");
        String longName = "a".repeat(256); // Assuming max length is 255
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, longName, "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(updatedProduct);
        });

        assertEquals("Product name must not exceed 255 characters", exception.getMessage());
    }

    @Test
    void testEditProductWithNonExistentProductId() {
        Category category = new Category(1L, "Electronics");
        Product updatedProduct = new Product(1L, "Galaxy S3", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(updatedProduct);
        });

        assertEquals("Invalid category ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteProductSuccessfully() {
        when(productRepository.existsById(1L)).thenReturn(true);

        when(importDetailRepository.existsByProductId(1L)).thenReturn(false);
        doNothing().when(importCartRepository).deleteByProductId_Id(1L);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            productService.deleteProductById(1L);
        });
    }

    @Test
    void testDeleteProductWithNonExistentProductId() {
        doThrow(new IllegalArgumentException("Invalid product ID: 999")).when(productRepository).deleteById(999L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProductById(999L);
        });

        assertEquals("Invalid product ID: 999", exception.getMessage());
    }

    @Test
    void testDeleteProductWithDatabaseError() {
        doThrow(new RuntimeException("Database error")).when(productRepository).deleteById(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProductById(1L);
        });

        assertEquals("Database error", exception.getMessage());
    }
}
