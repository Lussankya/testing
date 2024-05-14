package com.example.testing.service;

import com.example.testing.model.Supplier;
import com.example.testing.repo.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }
    public List<Supplier> searchSupplierByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }
    public boolean isSupplierNameExist(String name) {
        return supplierRepository.findByName(name) != null;
    }
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }
}
