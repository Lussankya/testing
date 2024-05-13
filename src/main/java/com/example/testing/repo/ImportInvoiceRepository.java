package com.example.testing.repo;

import com.example.testing.model.ImportInvoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ImportInvoiceRepository extends JpaRepository<ImportInvoice, Long> {
    List<ImportInvoice> findByImportDateBetween(Date startDate, Date endDate);
}
