package com.example.testing.service;


import com.example.testing.repo.ImportCartRepository;
import com.example.testing.repo.ImportDetailRepository;
import com.example.testing.repo.ImportInvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class ImportService {

    private final ImportCartRepository importCartRepository;
    private final ImportInvoiceRepository importInvoiceRepository;
    private final ImportDetailRepository importDetailRepository;

    public ImportService(ImportCartRepository importCartRepository,
                         ImportInvoiceRepository importInvoiceRepository,
                         ImportDetailRepository importDetailRepository) {
        this.importCartRepository = importCartRepository;
        this.importInvoiceRepository = importInvoiceRepository;
        this.importDetailRepository = importDetailRepository;
    }


}

