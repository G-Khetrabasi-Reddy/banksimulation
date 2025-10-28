package org.bank.service;

import org.bank.exception.AccountAlreadyClosedException;
import org.bank.exception.AccountNotFoundException;
import org.bank.exception.DuplicateAccountException;
import org.bank.exception.InvalidTransactionException;
import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.repository.AccountRepository;
import org.bank.repository.CustomerRepository;
import org.bank.serviceImpl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AccountServiceImpl class.
 * This class uses Mockito to mock repository dependencies
 * and test the service-layer business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepo; // Mocked AccountRepository

    @Mock
    private CustomerRepository customerRepo; // Mocked CustomerRepository

    @InjectMocks
    private AccountServiceImpl accountService; // The class we are testing

    private Account validAccount;
    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        // Create a standard customer that exists in the "database"
        existingCustomer = new Customer();
        existingCustomer.setCustomerId(1L);

        // Create a standard, valid account object for tests
        validAccount = new Account();
        validAccount.setCustomerId(1L);
        validAccount.setBalance(100.0);
        validAccount.setAccountType("SAVINGS");
        validAccount.setAccountNumber("ACC123456");
        validAccount.setIfscCode("BANK0123456"); // Valid IFSC
        validAccount.setStatus("ACTIVE");
    }

    // --- Tests for openAccount ---

    @Test
    void testOpenAccount_Success() {
        // Arrange
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.empty());
        when(accountRepo.addAccount(any(Account.class))).thenReturn(Optional.of(validAccount));

        // Act
        Account createdAccount = accountService.openAccount(validAccount);

        // Assert
        assertNotNull(createdAccount);
        assertEquals("ACC123456", createdAccount.getAccountNumber());
        verify(customerRepo).findById(1L);
        verify(accountRepo).findByAccountNumber("ACC123456");
        verify(accountRepo).addAccount(validAccount);
    }

    @Test
    void testOpenAccount_CustomerNotFound() {
        // Arrange
        when(customerRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> {
            accountService.openAccount(validAccount);
        });

        // Verify we never tried to save
        verify(accountRepo, never()).addAccount(any(Account.class));
    }

    @Test
    void testOpenAccount_DuplicateAccount() {
        // Arrange
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));
        // A duplicate account is found
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(new Account()));

        // Act & Assert
        assertThrows(DuplicateAccountException.class, () -> {
            accountService.openAccount(validAccount);
        });

        verify(accountRepo, never()).addAccount(any(Account.class));
    }

    @Test
    void testOpenAccount_InvalidIfsc() {
        // Arrange
        validAccount.setIfscCode("invalid-ifsc"); // Invalid format
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> {
            accountService.openAccount(validAccount);
        });
    }

    @Test
    void testOpenAccount_NegativeBalance() {
        // Arrange
        validAccount.setBalance(-500.0); // Negative balance
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));

        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> {
            accountService.openAccount(validAccount);
        });
    }

    // --- Tests for closeAccount ---

    @Test
    void testCloseAccount_Success() {
        // Arrange
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));
        when(accountRepo.updateAccountDetails(any(Account.class))).thenReturn(true);

        // Use ArgumentCaptor to verify the state of the account being saved
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        // Act
        accountService.closeAccount("ACC123456");

        // Assert
        verify(accountRepo).findByAccountNumber("ACC123456");
        // Capture the account object passed to updateAccountDetails
        verify(accountRepo).updateAccountDetails(accountCaptor.capture());

        // Verify the status was set to "CLOSED"
        assertEquals("CLOSED", accountCaptor.getValue().getStatus());
    }

    @Test
    void testCloseAccount_AccountNotFound() {
        // Arrange
        when(accountRepo.findByAccountNumber("ACC-NOT-FOUND")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.closeAccount("ACC-NOT-FOUND");
        });

        verify(accountRepo, never()).updateAccountDetails(any(Account.class));
    }

    @Test
    void testCloseAccount_AlreadyClosed() {
        // Arrange
        validAccount.setStatus("CLOSED");
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        // Act & Assert
        assertThrows(AccountAlreadyClosedException.class, () -> {
            accountService.closeAccount("ACC123456");
        });

        verify(accountRepo, never()).updateAccountDetails(any(Account.class));
    }

    // --- Tests for Getters ---

    @Test
    void testGetAccountDetails_Success() {
        // Arrange
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        // Act
        Account result = accountService.getAccountDetails("ACC123456");

        // Assert
        assertEquals(validAccount, result);
    }

    @Test
    void testGetAccountDetails_NotFound() {
        // Arrange
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountDetails("ACC-NOT-FOUND");
        });
    }

    @Test
    void testGetAccountBalance_Success() {
        // Arrange
        validAccount.setBalance(555.77);
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        // Act
        double balance = accountService.getAccountBalance("ACC123456");

        // Assert
        assertEquals(555.77, balance);
    }

    @Test
    void testGetAccountBalance_NotFound() {
        // Arrange
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountBalance("ACC-NOT-FOUND");
        });
    }

    // --- Tests for New/Updated Methods ---

    @Test
    void testGetAllAccounts() {
        // Arrange
        List<Account> accountList = List.of(validAccount);
        when(accountRepo.findAll()).thenReturn(accountList);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertEquals(accountList, result);
        verify(accountRepo).findAll();
    }

    @Test
    void testGetAccountsByCustomerId() {
        // Arrange
        long customerId = 1L;
        List<Account> accountList = List.of(validAccount);
        when(accountRepo.findByCustomerId(customerId)).thenReturn(accountList);

        // Act
        List<Account> result = accountService.getAccountsByCustomerId(customerId);

        // Assert
        assertEquals(accountList, result);
        verify(accountRepo).findByCustomerId(customerId);
    }

    @Test
    void testIsAccountOwnedByCustomer_True() {
        // Arrange
        long customerId = 1L;
        validAccount.setCustomerId(customerId);
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        // Act
        boolean isOwned = accountService.isAccountOwnedByCustomer("ACC123456", customerId);

        // Assert
        assertTrue(isOwned);
    }

    @Test
    void testIsAccountOwnedByCustomer_False_WrongOwner() {
        // Arrange
        long ownerId = 1L;
        long requesterId = 2L; // Different customer
        validAccount.setCustomerId(ownerId);
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        // Act
        boolean isOwned = accountService.isAccountOwnedByCustomer("ACC123456", requesterId);

        // Assert
        assertFalse(isOwned);
    }

    @Test
    void testIsAccountOwnedByCustomer_False_AccountNotFound() {
        // Arrange
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        // Act
        boolean isOwned = accountService.isAccountOwnedByCustomer("ACC-NOT-FOUND", 1L);

        // Assert
        assertFalse(isOwned);
    }
}
