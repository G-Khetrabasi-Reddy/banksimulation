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

    }

    // Tests for getCustomerById

    @Test
    void testGetCustomerById_Success() {
        when(service.findById(2L)).thenReturn(Optional.of(userCustomer));
        Response response = controller.getCustomerById(2L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Customer fetched successfully", entity.get("message"));
        assertEquals(userCustomer, entity.get("customer"));
        verify(service).findById(2L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(service.findById(99L)).thenThrow(new CustomerNotFoundException("Customer not found with ID: 99"));

        CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> {
            controller.getCustomerById(99L);
        });
        assertEquals("Customer not found with ID: 99", ex.getMessage());
    }

    @Test
    void testGetCustomerById_NullId() {
        InvalidCustomerDataException ex = assertThrows(InvalidCustomerDataException.class, () -> {
            controller.getCustomerById(null);
        });
        assertEquals("customerId is required", ex.getMessage());

        verify(service, never()).findById(anyLong());
    }

    // Tests for getAllCustomers

    @Test
    void testGetAllCustomers_Success() {
        List<Customer> customerList = List.of(userCustomer, adminCustomer);

        when(service.findAll()).thenReturn(customerList);
        Response response = controller.getAllCustomers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Customers fetched successfully", entity.get("message"));
        assertEquals(customerList, entity.get("customers"));
        verify(service).findAll();
    }

    @Test
    void testGetAllCustomers_EmptyList() {
        when(service.findAll()).thenReturn(Collections.emptyList());
        Response response = controller.getAllCustomers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals(Collections.emptyList(), entity.get("customers"));
        verify(service).findAll();
    }

    // Tests for updateCustomer

    @Test
    void testUpdateCustomer_AdminUpdatesOtherUser_Success() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(adminCustomer);
        when(service.updateCustomer(otherUser)).thenReturn(true);

        Response response = controller.updateCustomer(requestContext, otherUser);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals(true, entity.get("updated"));

        verify(service).updateCustomer(otherUser);
    }

    @Test
    void testUpdateCustomer_UserUpdatesSelf_Success() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.updateCustomer(userCustomer)).thenReturn(true);
        Response response = controller.updateCustomer(requestContext, userCustomer);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(service).updateCustomer(userCustomer);
    }

    @Test
    void testUpdateCustomer_UserUpdatesOtherUser_Forbidden() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);

        InvalidCustomerDataException ex = assertThrows(InvalidCustomerDataException.class, () -> {
            controller.updateCustomer(requestContext, otherUser);
        });

        assertEquals("You can only update your own profile.", ex.getMessage());
        verify(service, never()).updateCustomer(any(Customer.class));
    }
}

