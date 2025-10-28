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

/**
 * Unit tests for the CustomerServiceImpl class.
 * This class uses Mockito to mock the CustomerRepository dependency,
 * allowing us to test the service's business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepo; // Mocking the repository interface

    @InjectMocks
    private CustomerServiceImpl customerService; // Injecting mocks into our service

    private Customer validCustomer;

    @BeforeEach
    void setUp() {
        // Create a standard, valid customer object to be used in tests
        validCustomer = new Customer();
        validCustomer.setCustomerId(1L);
        validCustomer.setName("Valid Name");
        validCustomer.setPhoneNumber("9876543210"); // Valid Indian phone
        validCustomer.setEmail("valid@email.com");
        validCustomer.setAddress("123 Valid St");
        // Updated to 6 digits as per new validation
        validCustomer.setCustomerPin("123456");
        validCustomer.setAadharNumber("234567890123"); // Valid Aadhar
        validCustomer.setDob(LocalDate.of(1990, 1, 1)); // Non-null DOB
        validCustomer.setStatus("ACTIVE");
        // Updated to > 8 chars as per new validation
        validCustomer.setPassword("password123");
        validCustomer.setRole("USER");
    }

    // --- Tests for addCustomer ---

    @Test
    void testAddCustomer_Success() {
        // Arrange
        when(customerRepo.findAll()).thenReturn(List.of()); // No duplicates
        when(customerRepo.addCustomer(any(Customer.class))).thenReturn(Optional.of(validCustomer));

        // Act
        Optional<Customer> result = customerService.addCustomer(validCustomer);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Valid Name", result.get().getName());
        verify(customerRepo).findAll(); // Checked for duplicates
        verify(customerRepo).addCustomer(validCustomer); // Saved the customer
    }

    @Test
    void testAddCustomer_DuplicateEmail() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("valid@email.com"); // Same email
        existingCustomer.setAadharNumber("999999999999"); // Different Aadhar

        when(customerRepo.findAll()).thenReturn(List.of(existingCustomer));

        // Act & Assert
        assertThrows(DuplicateCustomerException.class, () -> {
            customerService.addCustomer(validCustomer);
        });

        // Verify we never tried to add the customer
        verify(customerRepo, never()).addCustomer(any(Customer.class));
    }

    @Test
    void testAddCustomer_DuplicateAadhar() {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("other@email.com"); // Different email
        existingCustomer.setAadharNumber("234567890123"); // Same Aadhar

        when(customerRepo.findAll()).thenReturn(List.of(existingCustomer));

        // Act & Assert
        assertThrows(DuplicateCustomerException.class, () -> {
            customerService.addCustomer(validCustomer);
        });

        // Verify we never tried to add the customer
        verify(customerRepo, never()).addCustomer(any(Customer.class));
    }

    @Test
    void testAddCustomer_InvalidName() {
        // Arrange
        validCustomer.setName("Invalid Name 123"); // Contains numbers

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for invalid name");
    }

    @Test
    void testAddCustomer_InvalidPhone() {
        // Arrange
        validCustomer.setPhoneNumber("1234567890"); // Starts with '1'

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for invalid phone");
    }

    @Test
    void testAddCustomer_InvalidAadhar() {
        // Arrange
        validCustomer.setAadharNumber("123456789012"); // Starts with '1'

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for invalid Aadhar");
    }

    @Test
    void testAddCustomer_BlankEmail() {
        // Arrange
        validCustomer.setEmail(" "); // Blank email

        // Act & Assert
        // Note: isNotBlank checks for null OR empty/whitespace string
        assertThrows(InvalidCustomerDataException.class, () -> {
            validCustomer.setEmail(null); // Test null case
            customerService.addCustomer(validCustomer);
        });
        assertThrows(InvalidCustomerDataException.class, () -> {
            validCustomer.setEmail("   "); // Test whitespace case
            customerService.addCustomer(validCustomer);
        });
    }

    // --- NEW TESTS for added validations ---
    @Test
    void testAddCustomer_InvalidPin_TooShort() {
        // Arrange
        validCustomer.setCustomerPin("123"); // Less than 6 digits

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for PIN less than 6 digits");
    }

    @Test
    void testAddCustomer_InvalidPin_NotDigits() {
        // Arrange
        validCustomer.setCustomerPin("12345a"); // Contains non-digit

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for non-digit PIN");
    }

    @Test
    void testAddCustomer_InvalidPassword_TooShort() {
        // Arrange
        validCustomer.setPassword("pass"); // Less than 8 characters

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for password less than 8 characters");
    }

    @Test
    void testAddCustomer_NullDob() {
        // Arrange
        validCustomer.setDob(null); // DOB is null

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.addCustomer(validCustomer);
        }, "Should throw for null Date of Birth");
    }
    // --- END NEW TESTS ---


    // --- Tests for findById ---

    @Test
    void testFindById_Success() {
        // Arrange
        when(customerRepo.findById(1L)).thenReturn(Optional.of(validCustomer));

        // Act
        Optional<Customer> result = customerService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validCustomer, result.get());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(customerRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findById(1L);
        });
    }

    // --- Tests for findAll ---

    @Test
    void testFindAll_Success() {
        // Arrange
        List<Customer> customerList = List.of(validCustomer);
        when(customerRepo.findAll()).thenReturn(customerList);

        // Act
        List<Customer> result = customerService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(customerList, result);
    }

    // --- Tests for updateCustomer ---

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        when(customerRepo.updateCustomer(validCustomer)).thenReturn(true);

        // Act
        boolean result = customerService.updateCustomer(validCustomer);

        // Assert
        assertTrue(result);
        verify(customerRepo).updateCustomer(validCustomer);
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Arrange
        when(customerRepo.updateCustomer(validCustomer)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(validCustomer);
        });
    }

    @Test
    void testUpdateCustomer_InvalidData() {
        // Arrange
        validCustomer.setPhoneNumber("555"); // Invalid phone

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.updateCustomer(validCustomer);
        });

        // Verify we never tried to update
        verify(customerRepo, never()).updateCustomer(any(Customer.class));
    }

    // --- NEW TESTS for update ---
    @Test
    void testUpdateCustomer_InvalidPin() {
        // Arrange
        validCustomer.setCustomerPin("123"); // Invalid PIN

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.updateCustomer(validCustomer);
        }, "Update should fail with invalid PIN");

        verify(customerRepo, never()).updateCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_NullDob() {
        // Arrange
        validCustomer.setDob(null); // Invalid DOB

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.updateCustomer(validCustomer);
        }, "Update should fail with null DOB");

        verify(customerRepo, never()).updateCustomer(any(Customer.class));
    }
    // --- END NEW TESTS for update ---


    // --- Tests for findByEmail ---

    @Test
    void testFindByEmail_Success() {
        // Arrange
        when(customerRepo.findByEmail("valid@email.com")).thenReturn(Optional.of(validCustomer));

        // Act
        Optional<Customer> result = customerService.findByEmail("valid@email.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validCustomer, result.get());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Arrange
        when(customerRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findByEmail("notfound@email.com");
        });
    }
}