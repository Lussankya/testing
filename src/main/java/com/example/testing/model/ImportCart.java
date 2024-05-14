package com.example.testing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "import_cart")
public class ImportCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product must not be null")
    private Product productId;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull(message = "Supplier must not be null")
    private Supplier supplierId;

    @Column(nullable = false)
    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be positive and whole number")
    private Integer quantity;

    @Column(nullable = false)
    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be positive and whole number")
    private Integer price;

    // Constructors, getters, setters

    public ImportCart() {
    }

    public ImportCart(Long id, Product productId, Supplier supplierId, Integer quantity, Integer price) {
        this.id = id;
        this.productId = productId;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
    }

    public Supplier getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Supplier supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
