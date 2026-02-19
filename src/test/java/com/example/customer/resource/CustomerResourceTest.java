package com.example.customer.resource;

import com.example.customer.dto.CreateCustomerRequest;
import com.example.customer.dto.UpdateCustomerRequest;
import com.example.customer.model.Customer;
import com.example.customer.service.CustomerService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@QuarkusTest
public class CustomerResourceTest {

    @InjectMock
    CustomerService customerService;

    private Customer testCustomer;
    private CreateCustomerRequest createRequest;
    private UpdateCustomerRequest updateRequest;

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

        createRequest = new CreateCustomerRequest();
        createRequest.setFirstName("John");
        createRequest.setFirstLastName("Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setAddress("123 Main St");
        createRequest.setPhone("+1234567890");
        createRequest.setCountryCode("US");

        updateRequest = new UpdateCustomerRequest();
        updateRequest.setEmail("new.email@example.com");
        updateRequest.setAddress("456 New St");
        updateRequest.setPhone("+9876543210");
        updateRequest.setCountryCode("CA");
    }

    @Test
    void testCreateCustomer_Success() {
        // Arrange
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(createRequest)
        .when()
            .post("/customers")
        .then()
            .statusCode(201);

        verify(customerService).createCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomer_BadRequest() {
        // Arrange
        when(customerService.createCustomer(any(Customer.class)))
            .thenThrow(new IllegalArgumentException("Invalid email"));

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(createRequest)
        .when()
            .post("/customers")
        .then()
            .statusCode(400);
    }

    @Test
    void testGetAllCustomers() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // Act & Assert
        Customer[] result = given()
            .when()
            .get("/customers")
        .then()
            .statusCode(200)
            .extract()
            .as(Customer[].class);

        assertEquals(1, result.length);
        assertEquals("John", result[0].getFirstName());
        verify(customerService).getAllCustomers();
    }

    @Test
    void testGetCustomersByCountry() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerService.getCustomersByCountry("US")).thenReturn(customers);

        // Act & Assert
        Customer[] result = given()
            .when()
            .get("/customers/country/US")
        .then()
            .statusCode(200)
            .extract()
            .as(Customer[].class);

        assertEquals(1, result.length);
        assertEquals("US", result[0].getCountryCode());
        verify(customerService).getCustomersByCountry("US");
    }

    @Test
    void testGetCustomerById_Success() {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);

        // Act & Assert
        Customer result = given()
            .when()
            .get("/customers/1")
        .then()
            .statusCode(200)
            .extract()
            .as(Customer.class);

        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        verify(customerService).getCustomerById(1L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        // Arrange
        when(customerService.getCustomerById(999L))
            .thenThrow(new jakarta.ws.rs.NotFoundException("Customer not found"));

        // Act & Assert
        given()
            .when()
            .get("/customers/999")
        .then()
            .statusCode(404);
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        Customer updatedCustomer = testCustomer;
        updatedCustomer.setEmail("new.email@example.com");
        when(customerService.updateCustomer(anyLong(), any(Customer.class))).thenReturn(updatedCustomer);

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/customers/1")
        .then()
            .statusCode(200);

        verify(customerService).updateCustomer(1L, any(Customer.class));
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Arrange
        when(customerService.updateCustomer(anyLong(), any(Customer.class)))
            .thenThrow(new jakarta.ws.rs.NotFoundException("Customer not found"));

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/customers/999")
        .then()
            .statusCode(404);
    }

    @Test
    void testDeleteCustomer_Success() {
        // Act & Assert
        given()
            .when()
            .delete("/customers/1")
        .then()
            .statusCode(204);

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Arrange
        doThrow(new jakarta.ws.rs.NotFoundException("Customer not found"))
            .when(customerService).deleteCustomer(999L);

        // Act & Assert
        given()
            .when()
            .delete("/customers/999")
        .then()
            .statusCode(404);
    }
}
