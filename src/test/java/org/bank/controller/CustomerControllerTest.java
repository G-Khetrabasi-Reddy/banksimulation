package org.bank.controller;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.bank.config.AuthenticationFilter;
import org.bank.exception.CustomerNotFoundException;
import org.bank.exception.InvalidCustomerDataException;
import org.bank.model.Customer;
import org.bank.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CustomerController.
 * This class uses Mockito to mock the CustomerService dependency.
 * It correctly handles Mockito's "strict stubbing" by only stubbing
 * the ContainerRequestContext in the tests that require it.
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService service; // Mocked service layer

    @Mock
    private ContainerRequestContext requestContext; // Mocked JAX-RS context

    @InjectMocks
    private CustomerController controller; // The controller to be tested

    // Test data
    private Customer adminCustomer;
    private Customer userCustomer;
    private Customer otherUser;

    @BeforeEach
    void setUp() {
        // Initialize common test data
        adminCustomer = new Customer(1L, "Admin", "111", "admin@bank.com", "Admin St", "111", "111", LocalDate.now(), "ACTIVE", "pass", "ADMIN");
        userCustomer = new Customer(2L, "User", "222", "user@bank.com", "User St", "222", "222", LocalDate.now(), "ACTIVE", "pass", "USER");
        otherUser = new Customer(3L, "Other", "333", "other@bank.com", "Other St", "333", "333", LocalDate.now(), "ACTIVE", "pass", "USER");

        // NOTE: We DO NOT stub 'requestContext.getProperty(...)' here.
        // Doing so would cause an UnnecessaryStubbingException in tests
        // that don't use the context (e.g., getCustomerById).
    }

    // --- Tests for getCustomerById ---

    @Test
    void testGetCustomerById_Success() {
        // Arrange
        when(service.findById(2L)).thenReturn(Optional.of(userCustomer));

        // Act
        Response response = controller.getCustomerById(2L);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Customer fetched successfully", entity.get("message"));
        assertEquals(userCustomer, entity.get("customer"));
        verify(service).findById(2L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        // Arrange
        // The service logic throws an exception, which the controller propagates
        when(service.findById(99L)).thenThrow(new CustomerNotFoundException("Customer not found with ID: 99"));

        // Act & Assert
        CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> {
            controller.getCustomerById(99L);
        });
        assertEquals("Customer not found with ID: 99", ex.getMessage());
    }

    @Test
    void testGetCustomerById_NullId() {
        // Arrange
        // No service mock needed, as this is a controller-level check

        // Act & Assert
        InvalidCustomerDataException ex = assertThrows(InvalidCustomerDataException.class, () -> {
            controller.getCustomerById(null);
        });
        assertEquals("customerId is required", ex.getMessage());

        // Verify the service was never called
        verify(service, never()).findById(anyLong());
    }

    // --- Tests for getAllCustomers ---

    @Test
    void testGetAllCustomers_Success() {
        // Arrange
        List<Customer> customerList = List.of(userCustomer, adminCustomer);
        when(service.findAll()).thenReturn(customerList);

        // Act
        Response response = controller.getAllCustomers();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Customers fetched successfully", entity.get("message"));
        assertEquals(customerList, entity.get("customers"));
        verify(service).findAll();
    }

    @Test
    void testGetAllCustomers_EmptyList() {
        // Arrange
        when(service.findAll()).thenReturn(Collections.emptyList());

        // Act
        Response response = controller.getAllCustomers();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals(Collections.emptyList(), entity.get("customers"));
        verify(service).findAll();
    }

    // --- Tests for updateCustomer (Security Logic) ---

    @Test
    void testUpdateCustomer_AdminUpdatesOtherUser_Success() {
        // Arrange
        // STUB CONTEXT HERE: The logged-in user is an ADMIN
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(adminCustomer);
        when(service.updateCustomer(otherUser)).thenReturn(true);

        // Act
        Response response = controller.updateCustomer(requestContext, otherUser);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals(true, entity.get("updated"));

        // Verify the security check passed and the service was called
        verify(service).updateCustomer(otherUser);
    }

    @Test
    void testUpdateCustomer_UserUpdatesSelf_Success() {
        // Arrange
        // STUB CONTEXT HERE: The logged-in user is a USER
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.updateCustomer(userCustomer)).thenReturn(true);

        // Act
        Response response = controller.updateCustomer(requestContext, userCustomer);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(service).updateCustomer(userCustomer);
    }

    @Test
    void testUpdateCustomer_UserUpdatesOtherUser_Forbidden() {
        // Arrange
        // STUB CONTEXT HERE: The logged-in user is a USER
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);

        // No service mock needed for update, as it should be blocked.

        // Act & Assert
        // Test that the controller's security check throws the correct exception
        InvalidCustomerDataException ex = assertThrows(InvalidCustomerDataException.class, () -> {
            controller.updateCustomer(requestContext, otherUser);
        });

        assertEquals("You can only update your own profile.", ex.getMessage());

        // Verify the service was NEVER called
        verify(service, never()).updateCustomer(any(Customer.class));
    }
}

