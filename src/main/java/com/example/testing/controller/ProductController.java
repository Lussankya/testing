package com.example.testing.controller;

import com.example.testing.dao.*;
import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.model.Supplier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

    @GetMapping("/list")
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "product-list";
    }
    @GetMapping("/index")
    public String showMainPage() {
        return "options";
    }

    @GetMapping("/search-page")
    public String showSearchProductPage() {
        return "search";
    }

    @PostMapping("/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String query) {
        List<Product> searchResults = productService.searchProductsByName(query);
        return searchResults;
    }

    @PostMapping("/delete/{productId}")
    @ResponseBody
    public String deleteProduct(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return "Product deleted successfully";
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
        return "redirect:/products/index";
    }






}
