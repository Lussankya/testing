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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void testAddProductWithDuplicateName() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "Smartphone", "Latest model", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));
        doThrow(new IllegalArgumentException("A product with the same name already exists in the given category."))
                .when(productService).addProduct(any(Product.class));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductSuccessfully() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "Smartphone", "Latest model", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/add-success"));
    }

    @Test
    void testAddProductWithMissingName() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "", "Latest model", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithMissingDescription() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "Smartphone", "", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "description"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithMissingCategory() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "Smartphone", "Latest model", null);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "category"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithNullFields() throws Exception {
        Product product = new Product(null, null, null, null);

        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/products/add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name", "description", "category"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithEmptyStrings() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(null, "", "", category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products.add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name", "description"))
                .andExpect(view().name("add-product"));
    }

    @Test
    void testAddProductWithExtremelyLongStrings() throws Exception {
        Category category = new Category(1L, "Electronics");
        String longString = "a".repeat(1000); // 1000 characters long
        Product product = new Product(null, longString, longString, category);

        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products.add")
                        .flashAttr("product", product))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasNoErrors("product")) // Assuming no validation constraints on length
                .andExpect(view().name("add-product"));
    }

    @Test
    void testEditProductSuccessfully() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product existingProduct = new Product(1L, "Old Name", "Old Description", category);
        Product updatedProduct = new Product(1L, "New Name", "New Description", category);

        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(existingProduct));
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));

        mockMvc.perform(post("/products/edit/1")
                        .flashAttr("product", updatedProduct))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/edit-success"));
    }

    @Test
    void testDeleteProductSuccessfully() throws Exception {
        mockMvc.perform(post("/products/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    @Test
    void testSearchProductByName() throws Exception {
        Category category = new Category(1L, "Electronics");
        Product product = new Product(1L, "Smartphone", "Latest model", category);

        when(productService.searchProductsByName("Smartphone")).thenReturn(Collections.singletonList(product));

        mockMvc.perform(post("/products/search")
                        .param("query", "Smartphone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Smartphone"))
                .andExpect(jsonPath("$[0].description").value("Latest model"))
                .andExpect(jsonPath("$[0].category.name").value("Electronics"));
    }

    @Test
    void testSearchProductByNameNotFound() throws Exception {
        when(productService.searchProductsByName("NonExistingProduct")).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/products/search")
                        .param("query", "NonExistingProduct"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
