package com.example.testing.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "import_invoice")
public class ImportInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date importDate;

    @Column(name = "total_imported_products")
    private Integer totalImportedProducts;

    @Column(name = "total_import_cost")
    private Integer totalImportCost;

    @OneToMany(mappedBy = "invoice")
    @JsonManagedReference
    private Set<ImportDetail> importDetails;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public Integer getTotalImportedProducts() {
        return totalImportedProducts;
    }

    public void setTotalImportedProducts(Integer totalImportedProducts) {
        this.totalImportedProducts = totalImportedProducts;
    }

    public Integer getTotalImportCost() {
        return totalImportCost;
    }

    public void setTotalImportCost(Integer totalImportCost) {
        this.totalImportCost = totalImportCost;
    }

    public Set<ImportDetail> getImportDetails() {
        return importDetails;
    }

    public void setImportDetails(Set<ImportDetail> importDetails) {
        this.importDetails = importDetails;
    }
}
