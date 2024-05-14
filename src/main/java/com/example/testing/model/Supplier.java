package com.example.testing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier name must not be empty")
    @Size(max = 255, message = "Supplier name must be <= 255 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Contact person must not be empty")
    @Size(max = 255, message = "Contact person must be <= 255 characters")
    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @NotBlank(message = "Phone number must not be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be <= 255 characters")
    @Column(nullable = false)
    private String email;

    public Supplier() {
    }

    public Supplier(Long id, String name, String contactPerson, String phone, String email) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
