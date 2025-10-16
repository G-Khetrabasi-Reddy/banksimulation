package org.bank.controller;

import jakarta.ws.rs.core.Response;
import org.bank.exception.CustomerNotFoundException;
import org.bank.model.Customer;
import org.bank.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class CustomerControllerTest {
    private CustomerService service;
    private CustomerController controller;
    private Customer sampleCustomer;

    @BeforeEach
    void setUp(){
        service = Mockito.mock(CustomerService.class);
        controller = new CustomerController(service);

        sampleCustomer = new Customer(
               1L, "John Doe", "9876543210", "john@example.com",
               "Bangalore", "1234", "Aadhar123",
                LocalDate.of(1995, 5, 15), "ACTIVE"
        );
    }

    // ================= SUCCESS SCENARIOS =================
    @Test
    void testAddCustomer(){
        when(service.addCustomer(any(Customer.class))).thenReturn(Optional.of(sampleCustomer));

        Response response = controller.addCustomer(sampleCustomer);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("Customer added successfully!", body.get("message"));
        assertNotNull(body.get("customer"));
    }

    @Test
    void testGetCustomerById(){
        when(service.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        Response response = controller.getCustomerById(Map.of("customerId", 1));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        Optional<Customer> customerOpt = (Optional<Customer>) body.get("customer");

        assertTrue(customerOpt.isPresent());
        assertEquals("John Doe", customerOpt.get().getName());

    }

    @Test
    void testGetAllCustomers() {
        when(service.findAll()).thenReturn(List.of(sampleCustomer));

        Response response = controller.getAllCustomers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertTrue(((List<Customer>) body.get("customers")).size() > 0);
    }

    @Test
    void testUpdateCustomer() {
        when(service.updateCustomer(any(Customer.class))).thenReturn(true);

        Response response = controller.updateCustomer(sampleCustomer);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("Customer updated successfully", body.get("message"));
        assertTrue((Boolean) body.get("updated"));
    }

    @Test
    void testDeleteCustomer() {
        when(service.deleteCustomer(1L)).thenReturn(true);

        Response response = controller.deleteCustomer(Map.of("customerId", 1));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("Customer deleted successfully", body.get("message"));
        assertTrue((Boolean) body.get("deleted"));
    }

    // ================= ERROR / FAILURE SCENARIOS =================
    @Test
    void testAddCustomerError() {
        when(service.addCustomer(any(Customer.class))).thenThrow(new RuntimeException("Duplicate customer"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> controller.addCustomer(sampleCustomer));
        assertEquals("Duplicate customer", exception.getMessage());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(service.findById(999L)).thenThrow(new CustomerNotFoundException("Customer not found with ID: 999"));

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> controller.getCustomerById(Map.of("customerId", 999))
        );

        assertEquals("Customer not found with ID: 999", exception.getMessage());
    }



    @Test
    void testUpdateCustomerFailure() {
        when(service.updateCustomer(any(Customer.class))).thenReturn(false);

        Response response = controller.updateCustomer(sampleCustomer);
        Map<String, Object> body = (Map<String, Object>) response.getEntity();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertFalse((Boolean) body.get("updated"));
    }

    @Test
    void testDeleteCustomerFailure() {
        when(service.deleteCustomer(999L)).thenReturn(false);

        Response response = controller.deleteCustomer(Map.of("customerId", 999));
        Map<String, Object> body = (Map<String, Object>) response.getEntity();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertFalse((Boolean) body.get("deleted"));
    }
}
