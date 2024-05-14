package com.example.testing.controller;

import com.example.testing.model.ImportCart;
import com.example.testing.model.ImportInvoice;
import com.example.testing.repo.ImportCartRepository;
import com.example.testing.repo.ImportDetailRepository;
import com.example.testing.repo.ImportInvoiceRepository;
import com.example.testing.service.ImportInvoiceService;
import com.example.testing.service.ImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImportController.class)
public class ImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportService importService;

    @MockBean
    private ImportCartRepository importCartRepository;

    @MockBean
    private ImportInvoiceRepository importInvoiceRepository;

    @MockBean
    private ImportInvoiceService importInvoiceService;

    @MockBean
    private ImportDetailRepository importDetailRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testFinalizeCartWithEmptyCart() throws Exception {
        when(importCartRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cart/finalize"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/cart"))
                .andExpect(flash().attribute("error", "The cart is empty."));
    }

    @Test
    void testViewImportInvoiceDetailsWithInvalidId() throws Exception {
        when(importInvoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/invoices/view/1"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("message", "Invalid invoice ID: 1"));
    }

    @Test
    void testViewImportInvoiceDetailsWithValidId() throws Exception {
        ImportInvoice invoice = new ImportInvoice();
        invoice.setId(1L);
        when(importInvoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(importDetailRepository.findAllByInvoice(invoice)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/invoices/view/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("invoice"))
                .andExpect(model().attributeExists("details"))
                .andExpect(view().name("import-invoice-details"));
    }

    @Test
    void testSearchInvoicesWithinDateRange() throws Exception {
        Date startDate = new Date();
        Date endDate = new Date();
        when(importInvoiceService.findInvoicesWithinDateRange(startDate, endDate)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/invoices/search")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testShowSearchInvoice() throws Exception {
        mockMvc.perform(get("/invoices/view-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("view-invoice"));
    }
}
