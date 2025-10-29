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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account validAccount;
    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        existingCustomer = new Customer();
        existingCustomer.setCustomerId(1L);

        validAccount = new Account();
        validAccount.setCustomerId(1L);
        validAccount.setBalance(100.0);
        validAccount.setAccountType("SAVINGS");
        validAccount.setAccountNumber("ACC123456");
        validAccount.setIfscCode("BANK0123456"); // Valid IFSC
        validAccount.setStatus("ACTIVE");
    }

    // Tests for openAccount

    @Test
    void testOpenAccount_Success() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.empty());
        when(accountRepo.addAccount(any(Account.class))).thenReturn(Optional.of(validAccount));

        Account createdAccount = accountService.openAccount(validAccount);

        assertNotNull(createdAccount);
        assertEquals("ACC123456", createdAccount.getAccountNumber());
        verify(customerRepo).findById(1L);
        verify(accountRepo).findByAccountNumber("ACC123456");
        verify(accountRepo).addAccount(validAccount);
    }

    @Test
    void testOpenAccount_CustomerNotFound() {
        when(customerRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InvalidTransactionException.class, () -> {
            accountService.openAccount(validAccount);
        });

        verify(accountRepo, never()).addAccount(any(Account.class));
    }

    @Test
    void testOpenAccount_DuplicateAccount() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(new Account()));

        assertThrows(DuplicateAccountException.class, () -> {
            accountService.openAccount(validAccount);
        });

        verify(accountRepo, never()).addAccount(any(Account.class));
    }

    @Test
    void testOpenAccount_InvalidIfsc() {
        validAccount.setIfscCode("invalid-ifsc"); // Invalid format
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));

        assertThrows(InvalidTransactionException.class, () -> {
            accountService.openAccount(validAccount);
        });
    }

    @Test
    void testOpenAccount_NegativeBalance() {
        validAccount.setBalance(-500.0); // Negative balance
        when(customerRepo.findById(1L)).thenReturn(Optional.of(existingCustomer));

        assertThrows(InvalidTransactionException.class, () -> {
            accountService.openAccount(validAccount);
        });
    }

    // Tests for closeAccount

    @Test
    void testCloseAccount_Success() {
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));
        when(accountRepo.updateAccountDetails(any(Account.class))).thenReturn(true);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        accountService.closeAccount("ACC123456");
        verify(accountRepo).findByAccountNumber("ACC123456");
        verify(accountRepo).updateAccountDetails(accountCaptor.capture());
        assertEquals("CLOSED", accountCaptor.getValue().getStatus());
    }

    @Test
    void testCloseAccount_AccountNotFound() {
        when(accountRepo.findByAccountNumber("ACC-NOT-FOUND")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountService.closeAccount("ACC-NOT-FOUND");
        });

        verify(accountRepo, never()).updateAccountDetails(any(Account.class));
    }

    @Test
    void testCloseAccount_AlreadyClosed() {
        validAccount.setStatus("CLOSED");
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        assertThrows(AccountAlreadyClosedException.class, () -> {
            accountService.closeAccount("ACC123456");
        });

        verify(accountRepo, never()).updateAccountDetails(any(Account.class));
    }

    // Tests for Getters

    @Test
    void testGetAccountDetails_Success() {
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));
        Account result = accountService.getAccountDetails("ACC123456");

        assertEquals(validAccount, result);
    }

    @Test
    void testGetAccountDetails_NotFound() {
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountDetails("ACC-NOT-FOUND");
        });
    }

    @Test
    void testGetAccountBalance_Success() {
        validAccount.setBalance(555.77);
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));
        double balance = accountService.getAccountBalance("ACC123456");

        assertEquals(555.77, balance);
    }

    @Test
    void testGetAccountBalance_NotFound() {
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountBalance("ACC-NOT-FOUND");
        });
    }

    // Tests for New/Updated Methods

    @Test
    void testGetAllAccounts() {
        List<Account> accountList = List.of(validAccount);
        when(accountRepo.findAll()).thenReturn(accountList);

        List<Account> result = accountService.getAllAccounts();

        assertEquals(accountList, result);
        verify(accountRepo).findAll();
    }

    @Test
    void testGetAccountsByCustomerId() {
        long customerId = 1L;
        List<Account> accountList = List.of(validAccount);
        when(accountRepo.findByCustomerId(customerId)).thenReturn(accountList);

        List<Account> result = accountService.getAccountsByCustomerId(customerId);

        assertEquals(accountList, result);
        verify(accountRepo).findByCustomerId(customerId);
    }

    @Test
    void testIsAccountOwnedByCustomer_True() {
        long customerId = 1L;
        validAccount.setCustomerId(customerId);
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        boolean isOwned = accountService.isAccountOwnedByCustomer("ACC123456", customerId);
        assertTrue(isOwned);
    }

    @Test
    void testIsAccountOwnedByCustomer_False_WrongOwner() {
        long ownerId = 1L;
        long requesterId = 2L; // Different customer
        validAccount.setCustomerId(ownerId);
        when(accountRepo.findByAccountNumber("ACC123456")).thenReturn(Optional.of(validAccount));

        boolean isOwned = accountService.isAccountOwnedByCustomer("ACC123456", requesterId);
        assertFalse(isOwned);
    }

    @Test
    void testIsAccountOwnedByCustomer_False_AccountNotFound() {
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        boolean isOwned = accountService.isAccountOwnedByCustomer("ACC-NOT-FOUND", 1L);
        assertFalse(isOwned);
    }
}
