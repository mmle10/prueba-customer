package com.example.customer.service;

import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerService {
    
    private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());
    
    @Inject
    CustomerRepository customerRepository;
    
    @Inject
    @RestClient
    CountryService countryService;
    
    @Transactional
    public Customer createCustomer(Customer customer) {
        // Validate email uniqueness
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }
        
        // Validate country code format before making API call
        if (customer.getCountryCode() != null && !customer.getCountryCode().matches("^[A-Z]{2}$")) {
            throw new IllegalArgumentException("Country code must be exactly 2 uppercase letters (ISO 3166-1 alpha-2)");
        }
        
        // Fetch and set demonym from external service
        try {
            String demonym = getDemonymForCountry(customer.getCountryCode());
            customer.setDemonym(demonym);
        } catch (Exception e) {
            LOGGER.warning("Could not fetch demonym for country " + customer.getCountryCode() + ": " + e.getMessage());
            // Continue without demonym if external service fails
        }
        
        customerRepository.persist(customer);
        return customer;
    }
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll().list();
    }
    
    public List<Customer> getCustomersByCountry(String countryCode) {
        return customerRepository.findByCountryCode(countryCode);
    }
    
    public Customer getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id);
        if (customer == null) {
            throw new NotFoundException("Customer not found with id: " + id);
        }
        return customer;
    }
    
    @Transactional
    public Customer updateCustomer(Long id, Customer customerUpdates) {
        Customer existingCustomer = getCustomerById(id);
        
        // Check if email is being updated and if it's unique
        if (customerUpdates.getEmail() != null && 
            !customerUpdates.getEmail().equals(existingCustomer.getEmail()) &&
            customerRepository.existsByEmail(customerUpdates.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customerUpdates.getEmail());
        }
        
        // Only allow updating specific fields
        if (customerUpdates.getEmail() != null) {
            existingCustomer.setEmail(customerUpdates.getEmail());
        }
        if (customerUpdates.getAddress() != null) {
            existingCustomer.setAddress(customerUpdates.getAddress());
        }
        if (customerUpdates.getPhone() != null) {
            existingCustomer.setPhone(customerUpdates.getPhone());
        }
        // Validate country code format before making API call
        if (customerUpdates.getCountryCode() != null) {
            if (!customerUpdates.getCountryCode().matches("^[A-Z]{2}$")) {
                throw new IllegalArgumentException("Country code must be exactly 2 uppercase letters (ISO 3166-1 alpha-2)");
            }
            existingCustomer.setCountryCode(customerUpdates.getCountryCode());
            
            // Update demonym if country changed
            try {
                String demonym = getDemonymForCountry(customerUpdates.getCountryCode());
                existingCustomer.setDemonym(demonym);
            } catch (Exception e) {
                LOGGER.warning("Could not fetch demonym for country " + customerUpdates.getCountryCode() + ": " + e.getMessage());
            }
        }
        
        return existingCustomer;
    }
    
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
    
    String getDemonymForCountry(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            LOGGER.warning("Country code is null or empty");
            return null;
        }
        
        try {
            LOGGER.info("Fetching demonym for country code: " + countryCode);
            List<CountryResponse> countries = countryService.getCountryByCode(countryCode);
            if (!countries.isEmpty() && countries.get(0).demonyms != null) {
                String demonym = countries.get(0).getDemonym();
                LOGGER.info("Successfully fetched demonym for " + countryCode + ": " + demonym);
                return demonym;
            } else {
                LOGGER.warning("No demonym found for country code: " + countryCode);
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to fetch country information for code '" + countryCode + "': " + e.getMessage());
            
            // Fallback: try to get demonym from a simple mapping
            String fallbackDemonym = getFallbackDemonym(countryCode);
            if (fallbackDemonym != null) {
                LOGGER.info("Using fallback demonym for " + countryCode + ": " + fallbackDemonym);
                return fallbackDemonym;
            }
        }
        return null;
    }
    
    private String getFallbackDemonym(String countryCode) {
        // Simple fallback mapping for common countries
        switch (countryCode) {
            case "US": return "American";
            case "GB": return "British";
            case "CA": return "Canadian";
            case "AU": return "Australian";
            case "DE": return "German";
            case "FR": return "French";
            case "ES": return "Spanish";
            case "IT": return "Italian";
            case "JP": return "Japanese";
            case "CN": return "Chinese";
            case "IN": return "Indian";
            case "BR": return "Brazilian";
            case "MX": return "Mexican";
            case "DO": return "Dominican";
            default: return null;
        }
    }
}
