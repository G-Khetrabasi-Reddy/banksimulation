package org.bank.service;

import org.bank.exception.CustomerNotFoundException;
import org.bank.exception.DuplicateCustomerException;
import org.bank.exception.InvalidCustomerDataException;
import org.bank.model.Customer;
import org.bank.repository.CustomerRepository;
import org.bank.serviceImpl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerRepository repository;
    private CustomerServiceImpl service;
    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);
        service = new CustomerServiceImpl(repository); // ✅ inject mock repo directly

        sampleCustomer = new Customer(
                1L, "John Doe", "9876543210", "john@gmail.com",
                "123 Street, Hyderabad", "1234", "234567890123",
                LocalDate.of(1998, 5, 15), "Active"
        );
    }

    // ---------------------- ADD CUSTOMER TESTS ----------------------

    @Test
    void testAddCustomer_Success() {
        when(repository.addCustomer(sampleCustomer)).thenReturn(Optional.ofNullable(sampleCustomer));

        Optional<Customer> result = service.addCustomer(sampleCustomer);

        assertTrue(result.isPresent());
        assertEquals(sampleCustomer, result.get());
        verify(repository).addCustomer(sampleCustomer);
    }

    @Test
    void testAddCustomer_EmptyOptionalThrowsDuplicateException() {
        when(repository.addCustomer(sampleCustomer)).thenReturn(Optional.empty());

        DuplicateCustomerException ex = assertThrows(
                DuplicateCustomerException.class,
                () -> service.addCustomer(sampleCustomer)
        );

        assertTrue(ex.getMessage().contains("Failed to add customer"));
        verify(repository).addCustomer(sampleCustomer);
    }

    @Test
    void testAddCustomer_DuplicateThrowsException() {
        when(repository.addCustomer(sampleCustomer))
                .thenThrow(new DuplicateCustomerException("Duplicate Aadhar"));

        DuplicateCustomerException ex = assertThrows(
                DuplicateCustomerException.class,
                () -> service.addCustomer(sampleCustomer)
        );

        assertTrue(ex.getMessage().contains("Duplicate Aadhar"));
        verify(repository).addCustomer(sampleCustomer);
    }

    // ---------------------- VALIDATION TESTS ----------------------

    @Test
    void testAddCustomer_InvalidName() {
        sampleCustomer.setName("John123");
        assertThrows(InvalidCustomerDataException.class, () -> service.addCustomer(sampleCustomer));
    }

    @Test
    void testAddCustomer_InvalidPhone() {
        sampleCustomer.setPhoneNumber("12345");
        assertThrows(InvalidCustomerDataException.class, () -> service.addCustomer(sampleCustomer));
    }

    @Test
    void testAddCustomer_InvalidAadhar() {
        sampleCustomer.setAadharNumber("123456789012");
        assertThrows(InvalidCustomerDataException.class, () -> service.addCustomer(sampleCustomer));
    }

    @Test
    void testAddCustomer_BlankEmail() {
        sampleCustomer.setEmail("   ");
        assertThrows(InvalidCustomerDataException.class, () -> service.addCustomer(sampleCustomer));
    }

    @Test
    void testAddCustomer_BlankAddress() {
        sampleCustomer.setAddress("");
        assertThrows(InvalidCustomerDataException.class, () -> service.addCustomer(sampleCustomer));
    }

    // ---------------------- FIND BY ID TESTS ----------------------

    @Test
    void testFindById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(sampleCustomer));

        Optional<Customer> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void testFindById_NotFoundThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.findById(1L));
    }

    // ---------------------- FIND ALL TEST ----------------------

    @Test
    void testFindAll_ReturnsList() {
        when(repository.findAll()).thenReturn(List.of(sampleCustomer));

        List<Customer> list = service.findAll();

        assertEquals(1, list.size());
        assertEquals("John Doe", list.get(0).getName());
    }

    // ---------------------- UPDATE CUSTOMER TESTS ----------------------

    @Test
    void testUpdateCustomer_Success() {
        when(repository.updateCustomer(sampleCustomer)).thenReturn(true);

        boolean result = service.updateCustomer(sampleCustomer);
        assertTrue(result);
    }

    @Test
    void testUpdateCustomer_NotFoundThrowsException() {
        when(repository.updateCustomer(sampleCustomer)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> service.updateCustomer(sampleCustomer));
    }

    // ---------------------- DELETE CUSTOMER TESTS ----------------------

    @Test
    void testDeleteCustomer_Success() {
        when(repository.deleteCustomer(1L)).thenReturn(true);

        boolean result = service.deleteCustomer(1L);

        assertTrue(result);
        verify(repository).deleteCustomer(1L);
    }

    @Test
    void testDeleteCustomer_NotFoundThrowsException() {
        when(repository.deleteCustomer(1L)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> service.deleteCustomer(1L));
    }
}
