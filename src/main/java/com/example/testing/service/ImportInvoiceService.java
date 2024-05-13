package com.example.testing.service;



import com.example.testing.model.ImportInvoice;

import com.example.testing.repo.ImportInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ImportInvoiceService {

    @Autowired
    private ImportInvoiceRepository importInvoiceRepository;

    public List<ImportInvoice> findInvoicesWithinDateRange(Date startDate, Date endDate) {
        return importInvoiceRepository.findByImportDateBetween(startDate, endDate);
    }

    public ImportInvoice findInvoiceById(Long id) {
        return importInvoiceRepository.findById(id).orElse(null);
    }
}

