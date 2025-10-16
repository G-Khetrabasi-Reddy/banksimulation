package org.bank.controller;

import org.bank.exception.*;
import org.bank.model.Account;
import org.bank.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    private AccountService service;
    private AccountController controller;
    private Account sampleAccount;

    @BeforeEach
    void setUp() {
        service = mock(AccountService.class);
        controller = new AccountController(service);

        sampleAccount = new Account(
                1L, 101L, LocalDateTime.now(), LocalDateTime.now(),
                1000.0, "SAVINGS", "MyAccount", "ACC12345",
                "ACTIVE", "ABCD0123456"
        );
    }

    // ---------------- Positive Tests ----------------
    @Test
    void testOpenAccount_Success() {
        when(service.openAccount(sampleAccount)).thenReturn(sampleAccount);

        Response response = controller.openAccount(sampleAccount);
        assertEquals(201, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals("Account opened successfully", body.get("message"));
        assertEquals(sampleAccount, body.get("account"));
    }

    @Test
    void testDeposit_Success() {
        when(service.getAccountBalance("ACC12345")).thenReturn(1500.0);

        Map<String, Object> request = Map.of(
                "accountNumber", "ACC12345",
                "amount", 500
        );

        Response response = controller.deposit(request);
        assertEquals(200, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertEquals(1500.0, body.get("balance"));
    }

    // ---------------- Negative / Exception Tests ----------------

    @Test
    void testOpenAccount_NullAccount() {
        when(service.openAccount(null))
                .thenThrow(new InvalidTransactionException("Account cannot be null."));

        InvalidTransactionException ex = assertThrows(
                InvalidTransactionException.class,
                () -> controller.openAccount(null)
        );
        assertEquals("Account cannot be null.", ex.getMessage());

    }

    @Test
    void testDeposit_MissingAccountNumber() {
        Map<String, Object> request = Map.of("amount", 500);
        assertThrows(InvalidTransactionException.class, () -> controller.deposit(request));
    }

    @Test
    void testDeposit_InvalidAmount() {
        Map<String, Object> request = Map.of(
                "accountNumber", "ACC12345",
                "amount", -100
        );
        assertThrows(InvalidTransactionException.class, () -> controller.deposit(request));
    }

    @Test
    void testWithdraw_MissingAccountNumber() {
        Map<String, Object> request = Map.of("amount", 500);
        assertThrows(InvalidTransactionException.class, () -> controller.withdraw(request));
    }

    @Test
    void testWithdraw_InvalidAmount() {
        Map<String, Object> request = Map.of(
                "accountNumber", "ACC12345",
                "amount", 0
        );
        assertThrows(InvalidTransactionException.class, () -> controller.withdraw(request));
    }

    @Test
    void testGetAccountDetails_MissingAccountNumber() {
        Map<String, String> request = Map.of(); // empty
        Response response = controller.getAccountDetails(request);

        assertEquals(400, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertTrue(body.get("error").toString().contains("Account number must be provided"));
    }

    @Test
    void testDeleteAccount_MissingAccountNumber() {
        Map<String, Object> request = Map.of(); // empty
        Response response = controller.deleteAccount(request);

        assertEquals(400, response.getStatus());
        Map<String, Object> body = (Map<String, Object>) response.getEntity();
        assertTrue(body.get("error").toString().contains("Account number must be provided"));
    }

    @Test
    void testDeposit_ServiceThrowsException() {
        Map<String, Object> request = Map.of(
                "accountNumber", "ACC12345",
                "amount", 500
        );
        doThrow(AccountNotFoundException.class).when(service).deposit("ACC12345", 500);

        assertThrows(AccountNotFoundException.class, () -> controller.deposit(request));
    }

    @Test
    void testWithdraw_ServiceThrowsException() {
        Map<String, Object> request = Map.of(
                "accountNumber", "ACC12345",
                "amount", 500
        );
        doThrow(InsufficientBalanceException.class).when(service).withdraw("ACC12345", 500);

        assertThrows(InsufficientBalanceException.class, () -> controller.withdraw(request));
    }
}