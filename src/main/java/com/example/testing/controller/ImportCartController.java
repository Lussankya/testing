package com.example.testing.controller;

import com.example.testing.model.ImportCart;
import com.example.testing.repo.ImportCartRepository;
import com.example.testing.service.ImportCartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public String addToCart(@Valid @ModelAttribute("importCart") ImportCart importCart,
                            BindingResult bindingResult,
                            Model model) {

        if (bindingResult.hasErrors()) {
            return "view-cart"; // Return to the form with validation errors
        }

        try {
            // Adding the product to the cart
            importCartService.addToCart(importCart.getProductId().getId(), importCart.getSupplierId().getId(), importCart.getQuantity(), importCart.getPrice());
            model.addAttribute("successMessage", "Product added to cart successfully!");
            return "redirect:/cart/view";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding product to cart: " + e.getMessage());
            return "product-search-to-cart"; // Stay on the current page and show error
        }
    }

    @GetMapping("/view")
    public String viewCart(Model model) {
        List<ImportCart> cartItems = importCartService.getAllCartItems();
        model.addAttribute("cartItems", cartItems);

        return "view-cart"; // the name of the HTML file
    }

    @PostMapping("/remove/{id}")
    public ModelAndView removeItemFromCart(@PathVariable("id") Long cartItemId, Model model) {
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
