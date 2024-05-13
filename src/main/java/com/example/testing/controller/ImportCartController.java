package com.example.testing.controller;


import com.example.testing.repo.ImportCartRepository;
import com.example.testing.service.ImportCartService;
import com.example.testing.model.ImportCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/cart")
@Controller
public class ImportCartController {
    @Autowired
    private ImportCartRepository importCartRepository;

    private final ImportCartService importCartService;

    public ImportCartController(ImportCartService importCartService) {
        this.importCartService = importCartService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("supplierId") Long supplierId,
                            @RequestParam("quantity") Integer quantity,
                            @RequestParam("price") Integer price,
                            RedirectAttributes redirectAttributes) {

        try {
            // Validating the input
            if (quantity <= 0 || price <= 0) {
                throw new IllegalArgumentException("Quantity and price must be positive numbers.");
            }

            // Adding the product to the cart
            importCartService.addToCart(productId, supplierId, quantity, price);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to cart successfully!");
            return "redirect:/cart/view"; // Redirect to view cart page
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding product to cart: " + e.getMessage());
            return e.getMessage(); // Stay on the current page and show error
        }
    }
    @GetMapping("/view")
    public String viewCart(Model model) {
        List<ImportCart> cartItems = importCartService.getAllCartItems();
        model.addAttribute("cartItems", cartItems);

        return "view-cart"; // the name of the HTML file
    }
    @PostMapping("/remove/{id}")
    public ModelAndView removeItemFromCart(@PathVariable("id") Long cartItemId) {
        try {
            importCartRepository.deleteById(cartItemId);
            return new ModelAndView("redirect:/cart/view");  // Redirect back to the cart page to see the updated list
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("message", "Error removing item: " + e.getMessage());
            return modelAndView;
        }
    }

}
