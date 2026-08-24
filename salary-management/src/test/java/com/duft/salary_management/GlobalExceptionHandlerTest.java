package com.duft.salary_management;

import com.duft.salary_management.DTO.ErrorResponse;
import com.duft.salary_management.Exceptions.DuplicateResourceException;
import com.duft.salary_management.Exceptions.GlobalExceptionHandler;
import com.duft.salary_management.Exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleResourceNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/employees/123");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(
                new ResourceNotFoundException("Employee not found"), request
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Employee not found", response.getBody().message());
    }

    @Test
    void testHandleDuplicateResource() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/employees");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateResource(
                new DuplicateResourceException("Duplicate email"), request
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflict", response.getBody().error());
        assertEquals("Duplicate email", response.getBody().message());
    }

    @Test
    void testHandleGenericException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/employees");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(
                new RuntimeException("Unexpected"), request
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().error());
    }
}
