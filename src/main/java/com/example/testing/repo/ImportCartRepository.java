package com.example.testing.repo;

import com.example.testing.model.ImportCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportCartRepository extends JpaRepository<ImportCart, Long> {
    boolean existsByProductId_Id(Long productId);
    void deleteByProductId_Id(Long productId);
}
