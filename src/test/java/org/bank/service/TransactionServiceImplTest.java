package org.bank.service;

import org.bank.config.DBConfig;
import org.bank.exception.InsufficientBalanceException;
import org.bank.exception.InvalidTransactionException;
import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.model.Transaction;
import org.bank.repository.AccountRepository;
import org.bank.repository.CustomerRepository;
import org.bank.repository.TransactionRepository;
import org.bank.service.NotificationService;
import org.bank.serviceImpl.TransactionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepo;
    @Mock
    private TransactionRepository transactionRepo;
    @Mock
    private CustomerRepository customerRepo;
    @Mock
    private NotificationService notificationService;
    @Mock
    private Connection conn;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private MockedStatic<DBConfig> dbConfigMock;

    private Customer senderCustomer;
    private Account senderAccount;
    private Customer receiverCustomer;
    private Account receiverAccount;

    @BeforeEach
    void setUp() throws SQLException {
        dbConfigMock = Mockito.mockStatic(DBConfig.class);
        dbConfigMock.when(DBConfig::getConnection).thenReturn(conn);

        senderCustomer = new Customer(1L, "Sender User", "9876543210", "sender@bank.com", "123 Send St", "1234", "111122223333", LocalDate.now(), "ACTIVE", "pass", "USER");
        senderAccount = new Account(101L, 1L, LocalDateTime.now(), LocalDateTime.now(), 1000.00, "SAVINGS", "Sender", "SENDER001", "ACTIVE", "BANK001");

        receiverCustomer = new Customer(2L, "Receiver User", "1234567890", "receiver@bank.com", "456 Recv St", "4321", "444455556666", LocalDate.now(), "ACTIVE", "pass", "USER");
        receiverAccount = new Account(102L, 2L, LocalDateTime.now(), LocalDateTime.now(), 500.00, "SAVINGS", "Receiver", "RECEIVER001", "ACTIVE", "BANK001");
    }

    @AfterEach
    void tearDown() {
        dbConfigMock.close();
    }

    @Test
    void testTransferMoney_Success() throws SQLException {
        when(accountRepo.findByAccountNumber(conn, "SENDER001")).thenReturn(Optional.of(senderAccount));
        when(accountRepo.findByAccountNumber(conn, "RECEIVER001")).thenReturn(Optional.of(receiverAccount));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(senderCustomer));
        when(customerRepo.findById(2L)).thenReturn(Optional.of(receiverCustomer));
        when(transactionRepo.saveTransaction(eq(conn), any(Transaction.class))).thenReturn(Optional.of(new Transaction()));

        Optional<Transaction> result = transactionService.transferMoney("SENDER001", "RECEIVER001", 100.00, "1234", "Test transfer", "ONLINE");

        assertTrue(result.isPresent());

        verify(accountRepo).updateBalance(conn, "SENDER001", 900.00);
        verify(accountRepo).updateBalance(conn, "RECEIVER001", 600.00);
        verify(transactionRepo).saveTransaction(eq(conn), any(Transaction.class));
        verify(conn).commit();
        verify(notificationService).emailAlert(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyDouble());
    }

    @Test
    void testTransferMoney_InsufficientBalance() throws SQLException {
        senderAccount.setBalance(50.00); // Not enough for a 100.00 transfer
        when(accountRepo.findByAccountNumber(conn, "SENDER001")).thenReturn(Optional.of(senderAccount));
        when(accountRepo.findByAccountNumber(conn, "RECEIVER001")).thenReturn(Optional.of(receiverAccount));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(senderCustomer));
        when(customerRepo.findById(2L)).thenReturn(Optional.of(receiverCustomer));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.transferMoney("SENDER001", "RECEIVER001", 100.00, "1234", "Test transfer", "ONLINE");
        });

        verify(accountRepo, never()).updateBalance(any(), anyString(), anyDouble());
        verify(transactionRepo, never()).saveTransaction(any(), any());
        verify(conn).rollback();
    }

    @Test
    void testTransferMoney_InvalidPin() throws SQLException {
        when(accountRepo.findByAccountNumber(conn, "SENDER001")).thenReturn(Optional.of(senderAccount));
        when(accountRepo.findByAccountNumber(conn, "RECEIVER001")).thenReturn(Optional.of(receiverAccount));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(senderCustomer));
        when(customerRepo.findById(2L)).thenReturn(Optional.of(receiverCustomer));

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.transferMoney("SENDER001", "RECEIVER001", 100.00, "9999", "Test transfer", "ONLINE");
        });

        verify(accountRepo, never()).updateBalance(any(), anyString(), anyDouble());
        verify(conn).rollback();
    }

    @Test
    void testTransferMoney_SameAccount() {
        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.transferMoney("SENDER001", "SENDER001", 100.00, "1234", "Test transfer", "ONLINE");
        });
    }
}