package com.example.testing.controller;

import com.example.testing.model.Supplier;
import com.example.testing.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/search-page")
    public String showSearchPage() {
        return "search-suppliers"; // return the name of your Thymeleaf template for the search page
    }

    @GetMapping("/search")
    public String searchSuppliersByName(@RequestParam String name, Model model) {
        List<Supplier> suppliers = supplierService.searchSupplierByName(name);
        model.addAttribute("suppliers", suppliers);
        return "search-suppliers";
    }

    @GetMapping("/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "add-supplier";
    }

    @PostMapping("/add")
    public String addSupplier(@Valid @ModelAttribute Supplier supplier, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "add-supplier";
        }
        if (supplierService.isSupplierNameExist(supplier.getName())) {
            model.addAttribute("errorMessage", "Supplier name already exists!");
            bindingResult.rejectValue("name", "duplicate", "Supplier name already exists!");
            return "add-supplier";
        }
        supplierService.saveSupplier(supplier);
        model.addAttribute("successMessage", "Supplier added successfully!");
        return "add-supplier-success";
    }

}
