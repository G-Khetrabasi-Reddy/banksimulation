package org.bank.controller;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.bank.config.AuthenticationFilter;
import org.bank.exception.InvalidTransactionException;
import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService service;

    @Mock
    private ContainerRequestContext requestContext;

    private AccountController controller;

    private Customer testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() throws Exception {
        controller = new AccountController(service);

        // Inject the mock context
        Field contextField = AccountController.class.getDeclaredField("requestContext");
        contextField.setAccessible(true);
        contextField.set(controller, requestContext);

        testUser = new Customer(1L, "Test User", "1234567890", "user@bank.com", "123 Main St", "1234", "111122223333", LocalDate.now(), "ACTIVE", "pass", "USER");
        testAccount = new Account(101L, 1L, null, null, 1000.0, "SAVINGS", "User Account", "ACC123", "ACTIVE", "BANK001");

    }

    @Test
    void testOpenAccount_Success() {
        when(service.openAccount(any(Account.class))).thenReturn(testAccount);
        Response response = controller.openAccount(testAccount);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Account opened successfully", entity.get("message"));
        assertEquals(testAccount, entity.get("account"));
        verify(service).openAccount(testAccount);
    }

    // Tests for the 'closeAccount'

    @Test
    void testCloseAccount_Success_UserOwnsAccount() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(testUser);

        Map<String, String> requestBody = Map.of("accountNumber", "ACC123");
        when(service.isAccountOwnedByCustomer("ACC123", 1L)).thenReturn(true);
        Response response = controller.closeAccount(requestBody);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Account closed successfully", ((Map<String, Object>) response.getEntity()).get("message"));
        verify(service).isAccountOwnedByCustomer("ACC123", 1L);
        verify(service).closeAccount("ACC123");
    }

    @Test
    void testCloseAccount_Forbidden_UserDoesNotOwnAccount() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(testUser);

        Map<String, String> requestBody = Map.of("accountNumber", "OTHER-ACC");
        when(service.isAccountOwnedByCustomer("OTHER-ACC", 1L)).thenReturn(false);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class, () -> {
            controller.closeAccount(requestBody);
        });

        assertEquals("You do not have permission to close this account.", ex.getMessage());
        verify(service).isAccountOwnedByCustomer("OTHER-ACC", 1L);
        verify(service, never()).closeAccount(anyString());
    }

    // Test for the new 'my-accounts'
    @Test
    void testGetMyAccounts_Success() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(testUser);

        List<Account> myAccounts = Collections.singletonList(testAccount);
        when(service.getAccountsByCustomerId(1L)).thenReturn(myAccounts);
        Response response = controller.getMyAccounts(requestContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Accounts fetched successfully", entity.get("message"));
        assertEquals(myAccounts, entity.get("accounts"));
        verify(service).getAccountsByCustomerId(1L);
    }


    @Test
    void testGetAccountDetails_Success() {
        when(service.getAccountDetails("ACC123")).thenReturn(testAccount);
        Response response = controller.getAccountDetails("ACC123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(testAccount, response.getEntity());
        verify(service).getAccountDetails("ACC123");
    }

    @Test
    void testGetAllAccounts_Success() {
        List<Account> allAccounts = List.of(testAccount, new Account());

        when(service.getAllAccounts()).thenReturn(allAccounts);
        Response response = controller.getAllAccounts();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals(allAccounts, entity.get("accounts"));
        verify(service).getAllAccounts();
    }

    @Test
    void testCheckBalance_Success() {
        when(service.getAccountBalance("ACC123")).thenReturn(1000.0);
        Response response = controller.checkBalance("ACC123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("ACC123", entity.get("accountNumber"));
        assertEquals(1000.0, entity.get("balance"));
        verify(service).getAccountBalance("ACC123");
    }

    @Test
    void testCheckBalance_NullAccountNumber() {
        Response response = controller.checkBalance(null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertTrue(entity.containsKey("error"));
        verify(service, never()).getAccountBalance(anyString());
    }
}