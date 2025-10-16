package org.bank.controller;

import jakarta.ws.rs.core.Response;
import org.bank.dto.TransactionResponse;
import org.bank.exception.*;
import org.bank.model.Transaction;
import org.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    private TransactionService transactionService;
    private TransactionController controller;
    private Transaction sampleTransaction;

    private static final String VALID_PIN = "1234";

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        controller = new TransactionController(transactionService);

        sampleTransaction = new Transaction();
        sampleTransaction.setTransactionId(1L);
        sampleTransaction.setSenderAccountId(10L);
        sampleTransaction.setReceiverAccountId(20L);
        sampleTransaction.setAmount(1000.0);
        sampleTransaction.setTransactionMode("ONLINE");
        sampleTransaction.setStatus("SUCCESS");
        sampleTransaction.setTransactionTime(LocalDateTime.now());
        sampleTransaction.setDescription("Sample Transfer");
    }

    // -------------------- Successful Tests --------------------

    @Test
    void testPerformTransfer_Success() {
        Map<String, Object> request = Map.of(
                "senderAccountNumber", "ACC1001",
                "receiverAccountNumber", "ACC1002",
                "amount", 500.0,
                "pin", VALID_PIN,
                "description", "Rent Payment",
                "transactionMode", "ONLINE"
        );

        when(transactionService.transferMoney(
                anyString(), anyString(), anyDouble(), eq(VALID_PIN), anyString(), anyString()))
                .thenReturn(Optional.of(sampleTransaction));
        when(transactionService.getAccountNumberById(10L)).thenReturn("ACC1001");
        when(transactionService.getAccountNumberById(20L)).thenReturn("ACC1002");

        Response response = controller.performTransfer(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("Transfer successful", body.get("message"));

        TransactionResponse tr = (TransactionResponse) body.get("transaction");
        assertEquals(1000.0, tr.getAmount());
        assertEquals("ONLINE", tr.getTransactionMode());
    }

    @Test
    void testGetTransactionById_Found() {
        Map<String, Object> request = Map.of("transactionId", 1L);
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(sampleTransaction));
        when(transactionService.getAccountNumberById(10L)).thenReturn("ACC1001");
        when(transactionService.getAccountNumberById(20L)).thenReturn("ACC1002");

        Response response = controller.getTransactionById(request);

        assertEquals(200, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("Transaction found successfully", body.get("message"));
        assertNotNull(body.get("transaction"));
    }

    @Test
    void testGetTransactionById_NotFound() {
        Map<String, Object> request = Map.of("transactionId", 99L);
        when(transactionService.getTransactionById(99L)).thenReturn(Optional.empty());

        Response response = controller.getTransactionById(request);

        assertEquals(404, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertTrue(body.get("error").toString().contains("99"));
    }

    @Test
    void testGetTransactionsByAccount_Success() {
        Map<String, Object> request = Map.of("accountNumber", "ACC1001");
        when(transactionService.getTransactionsByAccountNumber("ACC1001"))
                .thenReturn(List.of(sampleTransaction));
        when(transactionService.getAccountNumberById(10L)).thenReturn("ACC1001");
        when(transactionService.getAccountNumberById(20L)).thenReturn("ACC1002");

        Response response = controller.getTransactionsByAccount(request);

        assertEquals(200, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("Transactions fetched successfully for account ACC1001", body.get("message"));
        assertFalse(((List<?>) body.get("transactions")).isEmpty());
    }

    @Test
    void testGetAllTransactions_Success() {
        when(transactionService.getAllTransactions()).thenReturn(List.of(sampleTransaction));
        when(transactionService.getAccountNumberById(10L)).thenReturn("ACC1001");
        when(transactionService.getAccountNumberById(20L)).thenReturn("ACC1002");

        Response response = controller.getAllTransactions();

        assertEquals(200, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("All transactions fetched successfully", body.get("message"));
        assertNotNull(body.get("transactions"));
    }

    // -------------------- Negative / Invalid Inputs --------------------

    @Test
    void testPerformTransfer_InvalidAmount() {
        Map<String, Object> request = Map.of(
                "senderAccountNumber", "ACC1001",
                "receiverAccountNumber", "ACC1002",
                "amount", -100,
                "pin", VALID_PIN
        );

        when(transactionService.transferMoney(
                anyString(), anyString(), eq(-100.0), eq(VALID_PIN), anyString(), anyString()))
                .thenThrow(new InvalidTransactionException("Transaction amount must be positive."));

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> controller.performTransfer(request)
        );
        assertEquals("Transaction amount must be positive.", ex.getMessage());
    }

    @Test
    void testPerformTransfer_MissingReceiverAccount() {
        Map<String, Object> request = Map.of(
                "senderAccountNumber", "ACC1001",
                "amount", 500.0,
                "pin", VALID_PIN
        );

        when(transactionService.transferMoney(
                anyString(), isNull(), anyDouble(), eq(VALID_PIN), anyString(), anyString()))
                .thenThrow(new InvalidTransactionException("Receiver account number can't be null or empty."));

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> controller.performTransfer(request)
        );
        assertEquals("Receiver account number can't be null or empty.", ex.getMessage());
    }

    @Test
    void testGetTransactionsByAccount_MissingAccountNumber() {
        Map<String, Object> request = Map.of(); // Empty map

        when(transactionService.getTransactionsByAccountNumber(null))
                .thenThrow(new InvalidTransactionException("Account number must be provided."));

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> controller.getTransactionsByAccount(request)
        );
        assertEquals("Account number must be provided.", ex.getMessage());
    }

    @Test
    void testPerformTransfer_MissingSenderAccount() {
        Map<String, Object> request = Map.of(
                "senderAccountNumber", "",
                "receiverAccountNumber", "ACC1002",
                "amount", 500.0,
                "pin", VALID_PIN
        );

        when(transactionService.transferMoney(
                eq(""), anyString(), anyDouble(), eq(VALID_PIN), anyString(), anyString()))
                .thenThrow(new InvalidTransactionException("Sender account number can't be null or empty."));

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> controller.performTransfer(request)
        );
        assertEquals("Sender account number can't be null or empty.", ex.getMessage());
    }

    // -------------------- Exception Handling --------------------

    @Test
    void testPerformTransfer_ServiceThrowsException() {
        Map<String, Object> request = Map.of(
                "senderAccountNumber", "ACC1001",
                "receiverAccountNumber", "ACC1002",
                "amount", 500.0,
                "pin", VALID_PIN
        );

        when(transactionService.transferMoney(
                anyString(), anyString(), anyDouble(), eq(VALID_PIN), anyString(), anyString()))
                .thenThrow(new TransactionFailedException("Database error"));

        TransactionFailedException ex = assertThrows(
                TransactionFailedException.class,
                () -> controller.performTransfer(request)
        );

        assertEquals("Database error", ex.getMessage());
    }

    @Test
    void testGetTransactionById_ServiceThrowsException() {
        Map<String, Object> request = Map.of("transactionId", 5L);

        when(transactionService.getTransactionById(5L))
                .thenThrow(new RuntimeException("Transaction not found"));

        assertThrows(RuntimeException.class,
                () -> controller.getTransactionById(request));
    }
}
