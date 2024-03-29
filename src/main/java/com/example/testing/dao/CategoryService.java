package com.example.testing.dao;

import com.example.testing.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
