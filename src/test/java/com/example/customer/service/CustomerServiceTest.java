package com.example.customer.service;

import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class CustomerServiceTest {

    @InjectMock
    CustomerRepository customerRepository;

    @Inject
    CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setFirstLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setAddress("123 Main St");
        testCustomer.setPhone("+1234567890");
        testCustomer.setCountryCode("US");
    }

    // ==================== CREATE CUSTOMER TESTS ====================
    
    @Test
    void testCreateCustomer_Success() {
        // Arrange
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        Customer result = customerService.createCustomer(testCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(customerRepository, times(1)).persist(testCustomer);
    }

    @Test
    void testCreateCustomer_EmailAlreadyExists() {
        // Arrange
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.createCustomer(testCustomer)
        );
        assertEquals("Email already exists: john.doe@example.com", exception.getMessage());
        verify(customerRepository, never()).persist(any(Customer.class));
    }

    @Test
    void testCreateCustomer_InvalidCountryCode() {
        // Arrange
        Customer invalidCustomer = new Customer();
        invalidCustomer.setFirstName("John");
        invalidCustomer.setFirstLastName("Doe");
        invalidCustomer.setEmail("john.doe@example.com");
        invalidCustomer.setAddress("123 Main St");
        invalidCustomer.setPhone("+1234567890");
        invalidCustomer.setCountryCode("INVALID"); // Invalid country code
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.createCustomer(invalidCustomer)
        );
        assertEquals("Country code must be exactly 2 uppercase letters (ISO 3166-1 alpha-2)", exception.getMessage());
        verify(customerRepository, never()).persist(any(Customer.class));
    }

    // ==================== READ CUSTOMER TESTS ====================
    
    @Test
    void testGetCustomersByCountry() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findByCountryCode(anyString())).thenReturn(customers);

        // Act
        List<Customer> result = customerService.getCustomersByCountry("US");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("US", result.get(0).getCountryCode());
    }

    @Test
    void testGetCustomerById_Success() {
        // Arrange
        when(customerRepository.findById(anyLong())).thenReturn(testCustomer);

        // Act
        Customer result = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetCustomerById_NotFound() {
        // Arrange
        when(customerRepository.findById(anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(jakarta.ws.rs.NotFoundException.class,
                () -> customerService.getCustomerById(999L));
    }

    // ==================== UPDATE CUSTOMER TESTS ====================
    
    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        Customer existingCustomer = testCustomer;
        Customer updates = new Customer();
        updates.setEmail("new.email@example.com");
        updates.setAddress("456 New St");
        updates.setPhone("+9876543210");
        updates.setCountryCode("CA");

        when(customerRepository.findById(anyLong())).thenReturn(existingCustomer);

        // Act
        Customer result = customerService.updateCustomer(1L, updates);

        // Assert
        assertNotNull(result);
        assertEquals("new.email@example.com", result.getEmail());
        assertEquals("456 New St", result.getAddress());
        assertEquals("+9876543210", result.getPhone());
        assertEquals("CA", result.getCountryCode());
    }

    @Test
    void testUpdateCustomer_EmailAlreadyExists() {
        // Arrange
        Customer existingCustomer = testCustomer;
        existingCustomer.setEmail("old.email@example.com");
        
        Customer updates = new Customer();
        updates.setEmail("new.email@example.com");
        updates.setAddress("456 New St");
        
        when(customerRepository.findById(anyLong())).thenReturn(existingCustomer);
        when(customerRepository.existsByEmail("new.email@example.com")).thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.updateCustomer(1L, updates)
        );
        assertEquals("Email already exists: new.email@example.com", exception.getMessage());
    }

    @Test
    void testUpdateCustomer_InvalidCountryCode() {
        // Arrange
        Customer existingCustomer = testCustomer;
        Customer updates = new Customer();
        updates.setEmail("new.email@example.com");
        updates.setCountryCode("INVALID"); // Invalid country code
        
        when(customerRepository.findById(anyLong())).thenReturn(existingCustomer);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.updateCustomer(1L, updates)
        );
        assertEquals("Country code must be exactly 2 uppercase letters (ISO 3166-1 alpha-2)", exception.getMessage());
    }

    @Test
    void testUpdateCustomer_NullFields() {
        // Arrange
        Customer existingCustomer = testCustomer;
        Customer updates = new Customer(); // All fields null
        
        when(customerRepository.findById(anyLong())).thenReturn(existingCustomer);
        
        // Act
        Customer result = customerService.updateCustomer(1L, updates);
        
        // Assert
        assertNotNull(result);
        // Should not change any fields since all are null
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("123 Main St", result.getAddress());
        assertEquals("+1234567890", result.getPhone());
        assertEquals("US", result.getCountryCode());
    }

    // ==================== DELETE CUSTOMER TESTS ====================
    
    @Test
    void testDeleteCustomer_Success() {
        // Arrange
        when(customerRepository.findById(anyLong())).thenReturn(testCustomer);
        doNothing().when(customerRepository).delete(any());

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository).delete(testCustomer);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Arrange
        when(customerRepository.findById(anyLong())).thenReturn(null);
        
        // Act & Assert
        assertThrows(jakarta.ws.rs.NotFoundException.class,
                () -> customerService.deleteCustomer(999L));
    }

    // ==================== HELPER FUNCTION TESTS ====================
    
    @Test
    void testGetDemonymForCountry_NullCountryCode() {
        // Act
        String result = customerService.getDemonymForCountry(null);
        
        // Assert
        assertNull(result);
    }

    @Test
    void testGetDemonymForCountry_EmptyCountryCode() {
        // Act
        String result = customerService.getDemonymForCountry("");
        
        // Assert
        assertNull(result);
    }

    @Test
    void testGetDemonymForCountry_ValidCountry() {
        // Act
        String result = customerService.getDemonymForCountry("US");
        
        // Assert
        assertEquals("American", result); // Should use fallback
    }

    @Test
    void testGetDemonymForCountry_CountryServiceFailure() {
        // Act
        String result = customerService.getDemonymForCountry("DO");
        
        // Assert
        assertEquals("Dominican", result); // Should use fallback
    }
}
