package com.example.testing.service;
import com.example.testing.model.ImportCart;
import com.example.testing.model.Product;
import com.example.testing.model.Supplier;

import com.example.testing.repo.ImportCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportCartService {
    private final ImportCartRepository importCartRepository;
    private final ProductService productService;
    private final SupplierService supplierService;

    @Autowired
    public ImportCartService(ImportCartRepository importCartRepository,
                             ProductService productService,
                             SupplierService supplierSingleService) {
        this.importCartRepository = importCartRepository;
        this.productService = productService;
        this.supplierService = supplierSingleService;
    }

    public void addToCart( Long productId, Long supplierId,Integer quantity, Integer price) {
        Product product = productService.getProductById(productId);  // Ensure this method fetches the product
        Supplier supplier = supplierService.getSupplierById(supplierId); // Ensure this method fetches the supplier

        ImportCart importCart = new ImportCart();
        importCart.setProductId(product);
        importCart.setSupplierId(supplier);
        importCart.setQuantity(quantity);
        importCart.setPrice(price);
        importCartRepository.save(importCart);
    }
    public List<ImportCart> getAllCartItems() {
        return importCartRepository.findAll();
    }
}
