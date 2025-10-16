package org.bank.service;

import org.bank.exception.*;
import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.model.Transaction;
import org.bank.repository.AccountRepository;
import org.bank.repository.CustomerRepository;
import org.bank.repository.TransactionRepository;
import org.bank.serviceImpl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {

    private AccountRepository accountRepo;
    private CustomerRepository customerRepo;
    private TransactionRepository transactionRepo;
    private NotificationService notificationService;
    private TransactionServiceImpl transactionService;

    private Account senderAccount;
    private Account receiverAccount;
    private Customer senderCustomer;
    private Customer receiverCustomer;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        accountRepo = mock(AccountRepository.class);
        customerRepo = mock(CustomerRepository.class);
        transactionRepo = mock(TransactionRepository.class);
        notificationService = mock(NotificationService.class);
        transactionService = new TransactionServiceImpl(accountRepo, transactionRepo, notificationService, customerRepo);

        senderAccount = new Account();
        senderAccount.setAccountId(1L);
        senderAccount.setAccountNumber("ACC1001");
        senderAccount.setCustomerId(10L);
        senderAccount.setBalance(5000.0);
        senderAccount.setStatus("ACTIVE");

        receiverAccount = new Account();
        receiverAccount.setAccountId(2L);
        receiverAccount.setAccountNumber("ACC2002");
        receiverAccount.setCustomerId(20L);
        receiverAccount.setBalance(2000.0);
        receiverAccount.setStatus("ACTIVE");

        senderCustomer = new Customer();
        senderCustomer.setCustomerId(10L);
        senderCustomer.setCustomerPin("1234");
        senderCustomer.setEmail("sender@example.com");
        senderCustomer.setName("Sender");

        receiverCustomer = new Customer();
        receiverCustomer.setCustomerId(20L);
        receiverCustomer.setEmail("receiver@example.com");
        receiverCustomer.setName("Receiver");

        transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setAmount(1000.0);
    }

    // ✅ SUCCESS CASE
    @Test
    void testTransferMoney_SuccessfulTransaction() throws SQLException {
        when(accountRepo.findByAccountNumber(any(Connection.class), eq("ACC1001"))).thenReturn(Optional.of(senderAccount));
        when(accountRepo.findByAccountNumber(any(Connection.class), eq("ACC2002"))).thenReturn(Optional.of(receiverAccount));
        when(customerRepo.findById(10L)).thenReturn(Optional.of(senderCustomer));
        when(customerRepo.findById(20L)).thenReturn(Optional.of(receiverCustomer));
        when(transactionRepo.saveTransaction(any(Connection.class), any(Transaction.class)))
                .thenReturn(Optional.of(transaction));

        Optional<Transaction> result = transactionService.transferMoney(
                "ACC1001", "ACC2002", 1000.0, "1234", "Test Transfer", "ONLINE");

        assertTrue(result.isPresent());
        assertEquals(1000.0, result.get().getAmount());
        verify(accountRepo).updateBalance(any(Connection.class), eq("ACC1001"), eq(4000.0));
        verify(accountRepo).updateBalance(any(Connection.class), eq("ACC2002"), eq(3000.0));
        verify(notificationService).emailAlert(
                eq("sender@example.com"), eq("Sender"),
                eq("receiver@example.com"), eq("Receiver"),
                eq("ACC1001"), eq("ACC2002"), eq(1000.0)
        );
    }

    @Test
    void testTransferMoney_InvalidAmount() {
        assertThrows(InvalidTransactionException.class, () ->
                transactionService.transferMoney("ACC1001", "ACC2002", -500.0, "1234", "Invalid", "ONLINE"));
    }

    @Test
    void testTransferMoney_SameAccountNumbers() {
        assertThrows(InvalidTransactionException.class, () ->
                transactionService.transferMoney("ACC1001", "ACC1001", 1000.0, "1234", "Self Transfer", "ONLINE"));
    }

    @Test
    void testTransferMoney_InsufficientBalance() throws SQLException {
        senderAccount.setBalance(200.0);

        when(accountRepo.findByAccountNumber(any(Connection.class), eq("ACC1001"))).thenReturn(Optional.of(senderAccount));
        when(accountRepo.findByAccountNumber(any(Connection.class), eq("ACC2002"))).thenReturn(Optional.of(receiverAccount));
        when(customerRepo.findById(10L)).thenReturn(Optional.of(senderCustomer));
        when(customerRepo.findById(20L)).thenReturn(Optional.of(receiverCustomer));

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.transferMoney("ACC1001", "ACC2002", 1000.0, "1234", "Low balance", "ONLINE"));
    }

    @Test
    void testTransferMoney_InvalidPIN() throws SQLException {
        senderCustomer.setCustomerPin("9999");

        when(accountRepo.findByAccountNumber(any(Connection.class), eq("ACC1001"))).thenReturn(Optional.of(senderAccount));
        when(accountRepo.findByAccountNumber(any(Connection.class), eq("ACC2002"))).thenReturn(Optional.of(receiverAccount));
        when(customerRepo.findById(10L)).thenReturn(Optional.of(senderCustomer));
        when(customerRepo.findById(20L)).thenReturn(Optional.of(receiverCustomer));

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.transferMoney("ACC1001", "ACC2002", 500.0, "1234", "Wrong pin", "ONLINE"));
    }

    @Test
    void testTransferMoney_AccountNotFound() throws SQLException {
        when(accountRepo.findByAccountNumber(any(Connection.class), eq("ACC1001"))).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                transactionService.transferMoney("ACC1001", "ACC2002", 1000.0, "1234", "Not found", "ONLINE"));
    }

    @Test
    void testGetTransactionById() {
        when(transactionRepo.findByTransactionId(1L)).thenReturn(Optional.of(transaction));
        Optional<Transaction> result = transactionService.getTransactionById(1L);

        assertTrue(result.isPresent());
        assertEquals(1000.0, result.get().getAmount());
    }

    @Test
    void testGetTransactionsByAccountNumber() {
        when(accountRepo.findByAccountNumber("ACC1001")).thenReturn(Optional.of(senderAccount));
        when(transactionRepo.findByAccountId(1L)).thenReturn(List.of(transaction));

        List<Transaction> result = transactionService.getTransactionsByAccountNumber("ACC1001");

        assertEquals(1, result.size());
        assertEquals(1000.0, result.get(0).getAmount());
    }

    @Test
    void testGetAccountNumberById() {
        when(accountRepo.findAccountNumberById(1L)).thenReturn(Optional.of("ACC1001"));
        String result = transactionService.getAccountNumberById(1L);
        assertEquals("ACC1001", result);
    }

    @Test
    void testGetAccountNumberById_Unknown() {
        when(accountRepo.findAccountNumberById(99L)).thenReturn(Optional.empty());
        String result = transactionService.getAccountNumberById(99L);
        assertEquals("UNKNOWN", result);
    }
}
