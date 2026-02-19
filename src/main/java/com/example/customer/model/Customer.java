package com.example.customer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Schema(description = "Customer entity representing a customer in the system")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the customer", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false)
    @Schema(description = "First name of the customer", required = true)
    private String firstName;
    
    @Size(max = 100, message = "Second name must not exceed 100 characters")
    @Column(name = "second_name")
    @Schema(description = "Second name of the customer (optional)")
    private String secondName;
    
    @NotBlank(message = "First last name is required")
    @Size(max = 100, message = "First last name must not exceed 100 characters")
    @Column(name = "first_last_name", nullable = false)
    @Schema(description = "First last name of the customer", required = true)
    private String firstLastName;
    
    @Size(max = 100, message = "Second last name must not exceed 100 characters")
    @Column(name = "second_last_name")
    @Schema(description = "Second last name of the customer (optional)")
    private String secondLastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Email address of the customer", required = true)
    private String email;
    
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(name = "address", nullable = false)
    @Schema(description = "Address of the customer", required = true)
    private String address;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone must be valid (10-15 digits, optional +)")
    @Column(name = "phone", nullable = false)
    @Schema(description = "Phone number of the customer", required = true)
    private String phone;
    
    @NotBlank(message = "Country is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country must be a valid ISO 3166-1 alpha-2 code")
    @Column(name = "country_code", nullable = false)
    @Schema(description = "Country code in ISO 3166-1 alpha-2 format", required = true)
    private String countryCode;
    
    @Column(name = "demonym")
    @Schema(description = "Demonym of the customer's country (automatically populated)", accessMode = Schema.AccessMode.READ_ONLY)
    private String demonym;
    
    @Column(name = "created_at")
    @Schema(description = "Timestamp when customer was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Timestamp when customer was last updated", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    public Customer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Customer(String firstName, String secondName, String firstLastName, String secondLastName,
                   String email, String address, String phone, String countryCode) {
        this();
        this.firstName = firstName;
        this.secondName = secondName;
        this.firstLastName = firstLastName;
        this.secondLastName = secondLastName;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.countryCode = countryCode;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getSecondName() {
        return secondName;
    }
    
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
    
    public String getFirstLastName() {
        return firstLastName;
    }
    
    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }
    
    public String getSecondLastName() {
        return secondLastName;
    }
    
    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public String getDemonym() {
        return demonym;
    }
    
    public void setDemonym(String demonym) {
        this.demonym = demonym;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
