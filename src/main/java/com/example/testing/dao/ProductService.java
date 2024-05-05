package com.example.testing.dao;

import com.example.testing.dao.CategoryService;
import com.example.testing.dao.ProductRepository;
import com.example.testing.model.Category;
import com.example.testing.model.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryService categoryService,CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void addProduct(Product product) {
        Long categoryId = product.getCategory().getId();
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));

        // Check if a product with the same name already exists within the same category
        boolean productExists = productRepository.existsByNameAndCategory(product.getName(), category);

        // If the product with the same name already exists in the same category, handle it accordingly
        if (productExists) {
            throw new IllegalArgumentException("A product with the same name already exists in the given category.");
        }

        product.setCategory(category); // Set the fetched category to the product
        productRepository.save(product); // Save the new product
    }

    public List<Product> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }

    public String updateProduct(Long productId, Product productDetails) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Update product details
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());

        // Check if the category exists
        Category category = categoryService.getCategoryById(productDetails.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID."));
        product.setCategory(category);

        // Save the updated product
        productRepository.save(product);

        return "Product updated successfully.";
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public boolean isDuplicateProduct(Product product) {
        return productRepository.existsByNameAndCategory(product.getName(), product.getCategory());
    }
    public boolean isDuplicateProductExceptCurrent(Product product, Long currentProductId) {
        Product existingProduct = productRepository.findByNameAndCategoryId(product.getName(), product.getCategory().getId());
        return existingProduct != null && !existingProduct.getId().equals(currentProductId);
    }
}
