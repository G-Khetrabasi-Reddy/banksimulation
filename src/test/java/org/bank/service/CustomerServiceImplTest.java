package org.bank.service;

import org.bank.exception.CustomerNotFoundException;
import org.bank.exception.DuplicateCustomerException;
import org.bank.exception.InvalidCustomerDataException;
import org.bank.model.Customer;
import org.bank.repository.CustomerRepository;
import org.bank.serviceImpl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer validCustomer;

    @BeforeEach
    void setUp() {
        validCustomer = new Customer();
        validCustomer.setCustomerId(1L);
        validCustomer.setName("Valid Name");
        validCustomer.setPhoneNumber("9876543210");
        validCustomer.setEmail("valid@email.com");
        validCustomer.setAddress("123 Valid St");
        validCustomer.setCustomerPin("123456");
        validCustomer.setAadharNumber("234567890123");
        validCustomer.setDob(LocalDate.of(1990, 1, 1));
        validCustomer.setStatus("ACTIVE");
        validCustomer.setPassword("password123");
        validCustomer.setRole("USER");
    }

    // Tests for addCustomer

    @Test
    void testAddCustomer_Success() {
        when(customerRepo.findAll()).thenReturn(List.of());
        when(customerRepo.addCustomer(any(Customer.class))).thenReturn(Optional.of(validCustomer));

        Optional<Customer> result = customerService.addCustomer(validCustomer);

        assertTrue(result.isPresent());
        assertEquals("Valid Name", result.get().getName());
        verify(customerRepo).findAll();
        verify(customerRepo).addCustomer(validCustomer);
    }

    @Test
    void testAddCustomer_DuplicateEmail() {
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("valid@email.com");
        existingCustomer.setAadharNumber("999999999999");

        when(customerRepo.findAll()).thenReturn(List.of(existingCustomer));

        assertThrows(DuplicateCustomerException.class, () -> {
            customerService.addCustomer(validCustomer);
        });

        verify(customerRepo, never()).addCustomer(any(Customer.class));
    }

    @Test
    void testAddCustomer_DuplicateAadhar() {
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("other@email.com");
        existingCustomer.setAadharNumber("234567890123");
        when(customerRepo.findAll()).thenReturn(List.of(existingCustomer));

        assertThrows(DuplicateCustomerException.class, () -> {
            customerService.addCustomer(validCustomer);
        });

        verify(customerRepo, never()).addCustomer(any(Customer.class));
    }

    @Test
    void testAddCustomer_InvalidName() {
        validCustomer.setName("Invalid Name 123");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for invalid name");
    }

    @Test
    void testAddCustomer_InvalidPhone() {
        validCustomer.setPhoneNumber("1234567890");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for invalid phone");
    }

    @Test
    void testAddCustomer_InvalidAadhar() {
        validCustomer.setAadharNumber("123456789012");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for invalid Aadhar");
    }

    @Test
    void testAddCustomer_BlankEmail() {
        validCustomer.setEmail(" ");

        assertThrows(InvalidCustomerDataException.class, () -> {
            validCustomer.setEmail(null);
            customerService.addCustomer(validCustomer);
        });
        assertThrows(InvalidCustomerDataException.class, () -> {
            validCustomer.setEmail("   ");
            customerService.addCustomer(validCustomer);
        });
    }

    @Test
    void testAddCustomer_InvalidPin_TooShort() {
        validCustomer.setCustomerPin("123");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for PIN less than 6 digits");
    }

    @Test
    void testAddCustomer_InvalidPin_NotDigits() {
        validCustomer.setCustomerPin("12345a");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for non-digit PIN");
    }

    @Test
    void testAddCustomer_InvalidPassword_TooShort() {
        validCustomer.setPassword("pass");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for password less than 8 characters");
    }

    @Test
    void testAddCustomer_NullDob() {
        validCustomer.setDob(null);

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for null Date of Birth");
    }


    // Tests for findById

    @Test
    void testFindById_Success() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(validCustomer));

        Optional<Customer> result = customerService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(validCustomer, result.get());
    }

    @Test
    void testFindById_NotFound() {
        when(customerRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findById(1L);
        });
    }

    // Tests for findAll

    @Test
    void testFindAll_Success() {
        List<Customer> customerList = List.of(validCustomer);
        when(customerRepo.findAll()).thenReturn(customerList);

        List<Customer> result = customerService.findAll();

        assertEquals(1, result.size());
        assertEquals(customerList, result);
    }

    // Tests for updateCustomer

    @Test
    void testUpdateCustomer_Success() {
        when(customerRepo.updateCustomer(validCustomer)).thenReturn(true);
        boolean result = customerService.updateCustomer(validCustomer);

        assertTrue(result);
        verify(customerRepo).updateCustomer(validCustomer);
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(customerRepo.updateCustomer(validCustomer)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(validCustomer);
        });
    }

    @Test
    void testUpdateCustomer_InvalidData() {
        validCustomer.setPhoneNumber("555");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.updateCustomer(validCustomer);
        });

        verify(customerRepo, never()).updateCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_InvalidPin() {
        validCustomer.setCustomerPin("123");

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.updateCustomer(validCustomer);
        }, "Update should fail with invalid PIN");

        verify(customerRepo, never()).updateCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_NullDob() {
        validCustomer.setDob(null);

        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.updateCustomer(validCustomer);
        }, "Update should fail with null DOB");

        verify(customerRepo, never()).updateCustomer(any(Customer.class));
    }


    // Tests for findByEmail

    @Test
    void testFindByEmail_Success() {
        when(customerRepo.findByEmail("valid@email.com")).thenReturn(Optional.of(validCustomer));
        Optional<Customer> result = customerService.findByEmail("valid@email.com");

        assertTrue(result.isPresent());
        assertEquals(validCustomer, result.get());
    }

    @Test
    void testFindByEmail_NotFound() {
        when(customerRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findByEmail("notfound@email.com");
        });
    }
}