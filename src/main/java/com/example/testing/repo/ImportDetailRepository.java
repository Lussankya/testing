package com.example.testing.repo;


import com.example.testing.model.ImportDetail;
import com.example.testing.model.ImportInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, Long> {
    List<ImportDetail> findAllByInvoice(ImportInvoice invoice);
    boolean existsByProductId(Long productId);
    void deleteByProductId(Long productId);

}

