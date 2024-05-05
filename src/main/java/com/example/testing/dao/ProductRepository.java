package com.example.testing.dao;

import com.example.testing.model.Category;
import com.example.testing.model.Product;
import com.example.testing.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameAndCategory(String name, Category category);
    List<Product> findByNameContainingIgnoreCase(String name);
    Product findByName(String name);
    Product findByNameAndCategoryId(String name, Long categoryId);
}
