package com.example.testing.service;

import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.repo.CategoryRepository;
import com.example.testing.repo.ImportCartRepository;
import com.example.testing.repo.ImportDetailRepository;
import com.example.testing.repo.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ImportDetailRepository importDetailRepository;
    private final ImportCartRepository importCartRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryService categoryService, CategoryRepository categoryRepository, ImportDetailRepository importDetailRepository, ImportCartRepository importCartRepository) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.importDetailRepository = importDetailRepository;
        this.importCartRepository = importCartRepository;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void addProduct(Product product) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new IllegalArgumentException("Category is required");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (product.getDescription() == null || product.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        if (product.getName().length() > 255) {
            throw new IllegalArgumentException("Product name must not exceed 255 characters");
        }

        Long categoryId = product.getCategory().getId();
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));

        boolean productExists = productRepository.existsByNameAndCategory(product.getName(), category);

        if (productExists && (product.getId() == null || !product.getId().equals(productRepository.findByNameAndCategoryId(product.getName(), categoryId).getId()))) {
            throw new IllegalArgumentException("A product with the same name already exists in the given category.");
        }

        product.setCategory(category);
        productRepository.save(product);
    }

    public List<Product> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    @Transactional
    public void deleteProductById(Long productId) {
        // Check if the product is part of any invoice
        if (importDetailRepository.existsByProductId(productId)) {
            throw new IllegalArgumentException("Product cannot be deleted as it is part of an invoice.");
        }

        // Delete the product from the import cart
        if (importCartRepository.existsByProductId_Id(productId)) {
            importCartRepository.deleteByProductId_Id(productId);
        }

        // Delete the product itself
        productRepository.deleteById(productId);
    }


    public boolean isDuplicateProductExceptCurrent(Product product, Long currentProductId) {
        Product existingProduct = productRepository.findByNameAndCategoryId(product.getName(), product.getCategory().getId());
        return existingProduct != null && !existingProduct.getId().equals(currentProductId);
    }
}
