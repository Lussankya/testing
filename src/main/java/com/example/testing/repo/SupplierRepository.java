package com.example.testing.repo;
import com.example.testing.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Supplier findByName(String name);
    List<Supplier> findByNameContainingIgnoreCase(String name);

}
