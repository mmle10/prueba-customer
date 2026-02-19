package com.example.customer.repository;

import com.example.customer.model.Customer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
    
    public List<Customer> findByCountryCode(String countryCode) {
        return find("countryCode", countryCode).list();
    }
    
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}
