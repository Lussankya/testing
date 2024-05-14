package com.example.testing.controller;

import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.service.CategoryService;
import com.example.testing.service.ProductService;
import com.example.testing.service.SupplierService;
import com.example.testing.repo.CategoryRepository;
import com.example.testing.repo.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SupplierService supplierService;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService, categoryService, supplierService, categoryRepository, productRepository))
                .build();
    }

    // Add Product Tests

    @Test
    void testAddProductWithAllFieldsEmpty() throws Exception {
        Product product = new Product(null, "", "", null);

        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name", "description", "category"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductSuccessfully() throws Exception {
        Category category = new Category(1L, "Book");
        Product product = new Product(null, "sua", "sua", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/add-success"));
    }

    @Test
    void testAddProductWithMissingName() throws Exception {
        Category category = new Category(1L, "Book");
        Product product = new Product(null, "", "sua", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithDuplicateNameInSameCategory() throws Exception {
        Category category = new Category(1L, "Book");
        Product product = new Product(null, "sua", "sua2", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));
        doThrow(new IllegalArgumentException("A product with the same name already exists in the given category."))
                .when(productService).addProduct(any(Product.class));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithDuplicateNameInDifferentCategory() throws Exception {
        Category categoryBook = new Category(1L, "Book");
        Category categoryConsole = new Category(2L, "Console");
        Product product = new Product(null, "sua", "sua2", categoryConsole);

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(categoryBook, categoryConsole));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/add-success"));
    }

    @Test
    void testAddProductWithMissingDescription() throws Exception {
        Category category = new Category(1L, "Console");
        Product product = new Product(null, "PS4", "", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "description"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithNameExceedingMaxLength() throws Exception {
        Category category = new Category(1L, "Console");
        String longName = "a".repeat(256); // Assuming max length is 255
        Product product = new Product(null, longName, "dien thoai", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(view().name("add-product"));
    }
    @Test
    void testSearchProductWithEmptySearchBox() throws Exception {
        Product product1 = new Product(1L, "Product1", "Description1", new Category(1L, "Category1"));
        Product product2 = new Product(2L, "Product2", "Description2", new Category(2L, "Category2"));

        when(productService.searchProductsByName("")).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(post("/products/search")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product1"))
                .andExpect(jsonPath("$[1].name").value("Product2"));
    }

    @Test
    void testSearchProductWithValidInput() throws Exception {
        Product product1 = new Product(1L, "Tablet", "Description1", new Category(1L, "Electronics"));
        Product product2 = new Product(2L, "Toy", "Description2", new Category(2L, "Kids"));

        when(productService.searchProductsByName("T")).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(post("/products/search")
                        .param("query", "T"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tablet"))
                .andExpect(jsonPath("$[1].name").value("Toy"));
    }

    @Test
    void testSearchProductWithNonExistentProduct() throws Exception {
        when(productService.searchProductsByName("basketballs")).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/products/search")
                        .param("query", "basketballs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
    @Test
    void testEditProductWithMissingName() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(view().name("edit-product"));
    }

    @Test
    void testEditProductSuccessfully() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "iphone", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/edit-success"));
    }

    @Test
    void testEditProductWithDuplicateNameAndCategory() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "Galaxy S3", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));
        when(productService.isDuplicateProductExceptCurrent(updatedProduct, 1L)).thenReturn(true);

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(view().name("edit-product"));
    }

    @Test
    void testEditProductWithMissingCategory() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "Galaxy S3", "A high-end smartphone with advanced features", null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "category"))
                .andExpect(view().name("edit-product"));
    }

    @Test
    void testEditProductWithMissingDescription() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, "Galaxy S3", "", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "description"))
                .andExpect(view().name("edit-product"));
    }

    @Test
    void testEditProductWithNameExceedingMaxLength() throws Exception {
        Category category = new Category(1L, "Electronics");
        String longName = "a".repeat(256); // Assuming max length is 255
        Product existingProduct = new Product(1L, "Smartphone", "Latest model", category);
        Product updatedProduct = new Product(1L, longName, "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(view().name("edit-product"));
    }

    @Test
    void testEditProductWithNonExistentProductId() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product updatedProduct = new Product(1L, "Galaxy S3", "A high-end smartphone with advanced features", category);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().isNotFound());
    }

    // Delete Product Tests

    @Test
    void testDeleteProductSuccessfully() throws Exception {
        mockMvc.perform(post("/products/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    @Test
    void testDeleteProductWithNonExistentProductId() throws Exception {
        doThrow(new IllegalArgumentException("Invalid product ID: 999")).when(productService).deleteProductById(999L);

        mockMvc.perform(post("/products/delete/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid product ID: 999"));
    }

    @Test
    void testDeleteProductWithDatabaseError() throws Exception {
        doThrow(new RuntimeException("Database error")).when(productService).deleteProductById(1L);

        mockMvc.perform(post("/products/delete/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Database error"));
    }

}
