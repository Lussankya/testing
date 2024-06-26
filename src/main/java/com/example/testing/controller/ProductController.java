package com.example.testing.controller;

import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.service.CategoryService;
import com.example.testing.service.ProductService;
import com.example.testing.service.SupplierService;
import com.example.testing.repo.CategoryRepository;
import com.example.testing.repo.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, SupplierService supplierService, CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        Product product = new Product();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "add-product"; // Return to the form with validation errors
        }

        try {
            productService.addProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
            return "redirect:/products/add-success"; // Redirect after successful save
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories()); // Refresh the list to render the form again
            return "add-product"; // Stay on the add page and show the error
        }
    }

    @GetMapping("/add-success")
    public String addProductSuccess() {
        return "add-product-success"; // This is a new template that will show the success message and handle the redirect
    }

    @GetMapping("/index")
    public String showMainPage() {
        return "index";
    }

    @GetMapping("/search-product")
    public String searchProductPage(@RequestParam(required = false) Long supplierId, Model model) {
        model.addAttribute("supplierId", supplierId);  // Pass the supplier ID to the view
        return "product-search-to-cart";  // Name of the HTML file
    }

    @GetMapping("/search-page")
    public String showSearchProductPage() {
        return "search-product";
    }

    @PostMapping("/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String query) {
        List<Product> searchResults = productService.searchProductsByName(query);
        return searchResults;
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product. Please try again later.");
        }
    }

    @GetMapping("/edit/{productId}")
    public String editProduct(@PathVariable Long productId, Model model) {
        // Retrieve existing product from the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Retrieve all categories
        List<Category> categories = categoryRepository.findAll();

        // Add product and categories to the model
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);

        return "edit-product";
    }

    @PostMapping("/edit/{productId}")
    public String updateProduct(@PathVariable Long productId, @Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult, Model model) {
        // Check if product with the same name and category exists except the current one
        if (productService.isDuplicateProductExceptCurrent(product, productId)) {
            bindingResult.rejectValue("name", "duplicate", "Another product with the same name in this category already exists.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-product";
        }

        // Retrieve existing product
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Update product details
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        Category category = categoryService.getCategoryById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        existingProduct.setCategory(category);

        productRepository.save(existingProduct);
        return "redirect:/products/edit-success";
    }

    @GetMapping("/edit-success")
    public String editProductSuccess() {
        return "edit-product-success"; // This is a new template that will show the success message and handle the redirect
    }
}
