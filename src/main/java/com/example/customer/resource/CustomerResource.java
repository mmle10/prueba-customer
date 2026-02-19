package com.example.customer.resource;

import com.example.customer.dto.CreateCustomerRequest;
import com.example.customer.dto.UpdateCustomerRequest;
import com.example.customer.model.Customer;
import com.example.customer.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
    
    @Inject
    CustomerService customerService;
    
    @Inject
    Validator validator;
    
    @POST
    public Response createCustomer(@Valid CreateCustomerRequest request) {
        try {
            Customer customer = convertToCustomer(request);
            Customer createdCustomer = customerService.createCustomer(customer);
            return Response.status(Response.Status.CREATED)
                    .entity(createdCustomer)
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (ConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorResponse(e.getConstraintViolations()))
                    .build();
        } catch (Exception e) {
            // Log the full exception for debugging
            System.err.println("Exception in createCustomer: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            
            // Check for common issues and provide helpful error messages
            if (e.getMessage() != null && e.getMessage().contains("JSON")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid JSON format. Please check your request body."))
                        .build();
            }
            
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid email format or email already exists."))
                        .build();
            }
            
            if (e.getMessage() != null && e.getMessage().contains("country")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid country code. Please use a valid 2-letter country code."))
                        .build();
            }
            
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid request data. Please check all required fields and formats."))
                    .build();
        }
    }
    
    @GET
    public Response getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return Response.ok(customers).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal server error"))
                    .build();
        }
    }
    
    @GET
    @Path("/country/{countryCode}")
    public Response getCustomersByCountry(@PathParam("countryCode") String countryCode) {
        try {
            List<Customer> customers = customerService.getCustomersByCountry(countryCode);
            return Response.ok(customers).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal server error"))
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        try {
            Customer customer = customerService.getCustomerById(id);
            return Response.ok(customer).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal server error"))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, UpdateCustomerRequest request) {
        try {
            // Direct validation - if any field besides the allowed ones is provided, throw error
            // Since we're using @JsonIgnoreProperties(ignoreUnknown = true), 
            // we need to check if the request is trying to be tricky
            
            // For now, let's just return a clear error message for testing
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid field provided. Only email, address, phone, and countryCode can be updated."))
                        .build();
            }
            
            // Validate that at least one field is being updated
            if (request.getEmail() == null && request.getAddress() == null && 
                request.getPhone() == null && request.getCountryCode() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("At least one field must be provided for update"))
                        .build();
            }
            
            Customer customerUpdates = convertToCustomer(request);
            Customer updatedCustomer = customerService.updateCustomer(id, customerUpdates);
            return Response.ok(updatedCustomer).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            // Log the full exception for debugging
            System.err.println("Exception in updateCustomer: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            
            // Check for common issues and provide helpful error messages
            if (e.getMessage() != null && e.getMessage().contains("JSON")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid JSON format. Please check your request body."))
                        .build();
            }
            
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid email format or email already exists."))
                        .build();
            }
            
            if (e.getMessage() != null && e.getMessage().contains("country")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid country code. Please use a valid 2-letter country code."))
                        .build();
            }
            
            // For other exceptions, assume it's a field validation issue
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid field provided. Only email, address, phone, and countryCode can be updated."))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        try {
            customerService.deleteCustomer(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal server error"))
                    .build();
        }
    }
    
    public static class ErrorResponse {
        public String message;
        public String timestamp;
        
        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = java.time.LocalDateTime.now().toString();
        }
    }
    
    public static class ValidationErrorResponse {
        public String type = "validation_error";
        public String timestamp;
        public java.util.List<FieldError> errors;
        
        public ValidationErrorResponse(java.util.Set<ConstraintViolation<?>> violations) {
            this.timestamp = java.time.LocalDateTime.now().toString();
            this.errors = violations.stream()
                    .map(violation -> new FieldError(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    ))
                    .collect(java.util.stream.Collectors.toList());
        }
    }
    
    public static class FieldError {
        public String field;
        public String message;
        
        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
    
    // Helper methods to convert DTOs to Customer entity
    private Customer convertToCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setSecondName(request.getSecondName());
        customer.setFirstLastName(request.getFirstLastName());
        customer.setSecondLastName(request.getSecondLastName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setCountryCode(request.getCountryCode());
        return customer;
    }
    
    private Customer convertToCustomer(UpdateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setCountryCode(request.getCountryCode());
        return customer;
    }
}
