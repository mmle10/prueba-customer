package com.example.customer.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<Exception> {
    
    private static final Logger LOGGER = Logger.getLogger(ApplicationExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(Exception exception) {
        // Don't log 404s as severe errors
        if (exception.getMessage() != null && exception.getMessage().contains("404")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Log the full exception for debugging
        LOGGER.severe("Application exception caught: " + exception.getClass().getName() + ": " + exception.getMessage());
        exception.printStackTrace();
        
        // Handle invalid field errors (specific to update operations)
        if (exception instanceof InvalidFieldException) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        // Handle constraint violations with field-level errors
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(cve.getConstraintViolations());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        // Handle JSON parsing errors
        if (exception instanceof jakarta.json.JsonException || 
            (exception.getMessage() != null && 
             (exception.getMessage().contains("JSON") || 
              exception.getMessage().contains("json") ||
              exception.getMessage().contains("Unexpected character") ||
              exception.getMessage().contains("Unrecognized field")))) {
            ErrorResponse errorResponse = new ErrorResponse("Invalid JSON format. Please check your request body.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        // Handle illegal arguments (business logic errors)
        if (exception instanceof IllegalArgumentException) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        // For other exceptions, return internal server error
        ErrorResponse errorResponse = new ErrorResponse("Internal server error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
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
}
