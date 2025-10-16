package org.bank.service;

import org.bank.exception.*;
import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.repository.AccountRepository;
import org.bank.repository.CustomerRepository;
import org.bank.serviceImpl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    private AccountRepository accountRepo;
    private CustomerRepository customerRepo;
    private AccountServiceImpl service;
    private Account sampleAccount;

    @BeforeEach
    void setUp() {
        accountRepo = mock(AccountRepository.class);
        customerRepo = mock(CustomerRepository.class);
        service = new AccountServiceImpl(accountRepo, customerRepo); // inject both mocks

        sampleAccount = new Account(
                1L, 101L, LocalDateTime.now(), LocalDateTime.now(),
                1000.0, "SAVINGS", "MyAccount", "ACC12345",
                "ACTIVE", "ABCD0123456"
        );

        // Default mock: customer exists
        when(customerRepo.findById(101L)).thenReturn(Optional.of(new Customer()));
    }

    // ---------------- OPEN ACCOUNT ----------------
    @Test
    void testOpenAccount_Success() {
        when(accountRepo.findByAccountNumber(sampleAccount.getAccountNumber())).thenReturn(Optional.empty());
        when(accountRepo.addAccount(any(Account.class))).thenReturn(Optional.of(sampleAccount));

        Account created = service.openAccount(sampleAccount);

        assertEquals(sampleAccount, created);
        verify(accountRepo).addAccount(sampleAccount);
    }

    @Test
    void testOpenAccount_DuplicateAccount() {
        when(accountRepo.findByAccountNumber(sampleAccount.getAccountNumber())).thenReturn(Optional.of(sampleAccount));
        assertThrows(DuplicateAccountException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_InvalidCustomerId() {
        sampleAccount.setCustomerId(999L); // Non-existent customer
        when(customerRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_NegativeBalance() {
        sampleAccount.setBalance(-100);
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_InvalidIFSC() {
        sampleAccount.setIfscCode("INVALID");
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_NullAccount() {
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(null));
    }

    @Test
    void testOpenAccount_NullCustomerId() {
        sampleAccount.setCustomerId(0);
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_NullAccountType() {
        sampleAccount.setAccountType(null);
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_EmptyAccountType() {
        sampleAccount.setAccountType("");
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_NullAccountNumber() {
        sampleAccount.setAccountNumber(null);
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_EmptyAccountNumber() {
        sampleAccount.setAccountNumber(" ");
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_NullIFSCCode() {
        sampleAccount.setIfscCode(null);
        assertThrows(InvalidTransactionException.class, () -> service.openAccount(sampleAccount));
    }

    @Test
    void testOpenAccount_EmptyStatus_DefaultActive() {
        sampleAccount.setStatus(null);
        when(accountRepo.findByAccountNumber(sampleAccount.getAccountNumber())).thenReturn(Optional.empty());
        when(accountRepo.addAccount(sampleAccount)).thenReturn(Optional.of(sampleAccount));

        Account created = service.openAccount(sampleAccount);
        assertEquals("ACTIVE", created.getStatus());
    }

    @Test
    void testOpenAccount_BlankStatus_DefaultActive() {
        sampleAccount.setStatus("  ");
        when(accountRepo.findByAccountNumber(sampleAccount.getAccountNumber())).thenReturn(Optional.empty());
        when(accountRepo.addAccount(sampleAccount)).thenReturn(Optional.of(sampleAccount));

        Account created = service.openAccount(sampleAccount);
        assertEquals("ACTIVE", created.getStatus());
    }

    @Test
    void testOpenAccount_RepositoryFailure() {
        when(accountRepo.findByAccountNumber(sampleAccount.getAccountNumber())).thenReturn(Optional.empty());
        when(accountRepo.addAccount(sampleAccount)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.openAccount(sampleAccount));
    }

    // ---------------- CLOSE ACCOUNT ----------------
    @Test
    void testCloseAccount_Success() {
        sampleAccount.setStatus("ACTIVE");
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        when(accountRepo.updateAccountDetails(sampleAccount)).thenReturn(true);

        assertDoesNotThrow(() -> service.closeAccount("ACC12345"));
        assertEquals("CLOSED", sampleAccount.getStatus());
    }

    @Test
    void testCloseAccount_AlreadyClosed() {
        sampleAccount.setStatus("CLOSED");
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        assertThrows(AccountAlreadyClosedException.class, () -> service.closeAccount("ACC12345"));
    }

    @Test
    void testCloseAccount_NotFound() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.closeAccount("ACC12345"));
    }

    // ---------------- DEPOSIT ----------------
    @Test
    void testDeposit_Success() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        when(accountRepo.updateBalance("ACC12345", 1500.0)).thenReturn(true);

        service.deposit("ACC12345", 500);
        verify(accountRepo).updateBalance("ACC12345", 1500.0);
    }

    @Test
    void testDeposit_InvalidAmount() {
        assertThrows(InvalidTransactionException.class, () -> service.deposit("ACC12345", -100));
    }

    @Test
    void testDeposit_ClosedAccount() {
        sampleAccount.setStatus("CLOSED");
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        assertThrows(AccountAlreadyClosedException.class, () -> service.deposit("ACC12345", 100));
    }

    @Test
    void testDeposit_NotFound() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.deposit("ACC12345", 100));
    }

    // ---------------- WITHDRAW ----------------
    @Test
    void testWithdraw_Success() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        when(accountRepo.updateBalance("ACC12345", 500.0)).thenReturn(true);

        service.withdraw("ACC12345", 500);
        verify(accountRepo).updateBalance("ACC12345", 500.0);
    }

    @Test
    void testWithdraw_InsufficientBalance() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        assertThrows(InsufficientBalanceException.class, () -> service.withdraw("ACC12345", 2000));
    }

    @Test
    void testWithdraw_NotFound() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.withdraw("ACC12345", 100));
    }

    // ---------------- GET ACCOUNT DETAILS ----------------
    @Test
    void testGetAccountDetails_Success() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        Account result = service.getAccountDetails("ACC12345");
        assertEquals("ACC12345", result.getAccountNumber());
    }

    @Test
    void testGetAccountDetails_NotFound() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.getAccountDetails("ACC12345"));
    }

    // ---------------- GET BALANCE ----------------
    @Test
    void testGetAccountBalance_Success() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        double balance = service.getAccountBalance("ACC12345");
        assertEquals(1000.0, balance);
    }

    @Test
    void testGetAccountBalance_NotFound() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.getAccountBalance("ACC12345"));
    }

    // ---------------- DELETE ACCOUNT ----------------
    @Test
    void testDeleteAccount_Success() {
        sampleAccount.setStatus("CLOSED");
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        when(accountRepo.deleteAccount("ACC12345")).thenReturn(true);
        assertDoesNotThrow(() -> service.deleteAccount("ACC12345"));
    }

    @Test
    void testDeleteAccount_ActiveAccount() {
        sampleAccount.setStatus("ACTIVE");
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.of(sampleAccount));
        assertThrows(InvalidTransactionException.class, () -> service.deleteAccount("ACC12345"));
    }

    @Test
    void testDeleteAccount_NotFound() {
        when(accountRepo.findByAccountNumber("ACC12345")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.deleteAccount("ACC12345"));
    }
}
