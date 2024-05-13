package com.example.testing.repo;


import com.example.testing.model.ImportDetail;
import com.example.testing.model.ImportInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, Long> {
    // Here, you can define any additional methods you might need.
    List<ImportDetail> findAllByInvoice(ImportInvoice invoice);
}

