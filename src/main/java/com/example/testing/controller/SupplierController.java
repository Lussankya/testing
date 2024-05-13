package com.example.testing.controller;

import com.example.testing.service.SupplierService;
import com.example.testing.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
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
    public String addSupplier(@ModelAttribute Supplier supplier, Model model) {
        if (supplierService.isSupplierNameExist(supplier.getName())) {
            model.addAttribute("errorMessage", "Supplier name already exists!");
            return "add-supplier";
        }
        if (supplier.getPhone().length() != 10 || !supplier.getPhone().matches("\\d{10}")) {
            model.addAttribute("errorMessage", "Phone number must be exactly 10 digits!");
            return "add-supplier";
        }
        supplierService.saveSupplier(supplier);
        model.addAttribute("successMessage", "Supplier added successfully!");
        return "add-supplier-success";
    }
}

