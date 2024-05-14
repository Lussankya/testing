package com.example.testing.controller;



import com.example.testing.service.*;
import com.example.testing.model.ImportCart;
import com.example.testing.model.ImportDetail;
import com.example.testing.model.ImportInvoice;
import com.example.testing.repo.ImportCartRepository;
import com.example.testing.repo.ImportDetailRepository;
import com.example.testing.repo.ImportInvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
public class ImportController {

    private final ImportService importService;
    @Autowired
    private ImportCartRepository importCartRepository;
    @Autowired
    private ImportInvoiceRepository importInvoiceRepository;
    @Autowired
    private ImportInvoiceService importInvoiceService;
    @Autowired
    private ImportDetailRepository importDetailRepository;
    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @Transactional
    @GetMapping("/cart/finalize")
    public ModelAndView finalizeCart() {
        List<ImportCart> cartItems = importCartRepository.findAll();
        if (cartItems.isEmpty()) {
            ModelAndView emptyCartView = new ModelAndView("redirect:/cart");
            emptyCartView.addObject("error", "The cart is empty.");
            return emptyCartView;
        }

        ImportInvoice invoice = new ImportInvoice();
        invoice.setImportDate(new Date());
        invoice = importInvoiceRepository.save(invoice);

        int totalProducts = 0;
        int totalCost = 0;

        for (ImportCart cartItem : cartItems) {
            ImportDetail detail = new ImportDetail();
            detail.setInvoice(invoice);
            detail.setProduct(cartItem.getProductId());
            detail.setSupplier(cartItem.getSupplierId());
            detail.setQuantity(cartItem.getQuantity());
            detail.setPrice(cartItem.getPrice());
            importDetailRepository.save(detail);

            totalProducts += cartItem.getQuantity();
            totalCost += cartItem.getPrice() * cartItem.getQuantity();
        }

        invoice.setTotalImportedProducts(totalProducts);
        invoice.setTotalImportCost(totalCost);
        invoice = importInvoiceRepository.save(invoice);
        importCartRepository.deleteAll();

        // Redirect to the invoice details view with the newly created invoice ID
        return new ModelAndView("redirect:/invoices/view/" + invoice.getId());
    }
    @GetMapping("/invoices/view/{invoiceId}")
    public String viewImportInvoiceDetails(@PathVariable Long invoiceId, Model model) {
        ImportInvoice invoice = importInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invoice ID: " + invoiceId));
        model.addAttribute("invoice", invoice);
        model.addAttribute("details", importDetailRepository.findAllByInvoice(invoice));
        return "import-invoice-details";
    }
    @GetMapping("/invoices/search")
    @ResponseBody
    public List<ImportInvoice> searchInvoices(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                              @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return importInvoiceService.findInvoicesWithinDateRange(startDate, endDate);
    }
    @GetMapping("/invoices/view-page")
    public String showSearchInvoice () {
        return "view-invoice";
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", e.getMessage());
        return mav;
    }
}

