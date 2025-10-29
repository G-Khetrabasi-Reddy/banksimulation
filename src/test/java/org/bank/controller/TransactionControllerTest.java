package org.bank.controller;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.bank.config.AuthenticationFilter;
import org.bank.dto.TransactionResponse;
import org.bank.exception.InvalidTransactionException;
import org.bank.exception.TransactionNotFoundException;
import org.bank.model.Customer;
import org.bank.model.Transaction;
import org.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService service;

    @Mock
    private ContainerRequestContext requestContext;

    private TransactionController controller;

    // Test Data
    private Customer userCustomer;
    private Customer adminCustomer;
    private Transaction testTransaction;
    private Map<String, Object> transferRequestBody;

    @BeforeEach
    void setUp() throws Exception {
        controller = new TransactionController(service);

        try {
            Field contextField = TransactionController.class.getDeclaredField("requestContext");
            contextField.setAccessible(true);
            contextField.set(controller, requestContext);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to inject mock requestContext via reflection: " + e.getMessage());
        }

        // Initialize test data
        userCustomer = new Customer(1L, "User", "123", "user@bank.com", "User St", "123", "123", LocalDate.now(), "ACTIVE", "pass", "USER");
        adminCustomer = new Customer(99L, "Admin", "999", "admin@bank.com", "Admin St", "999", "999", LocalDate.now(), "ACTIVE", "pass", "ADMIN");
        testTransaction = new Transaction(100L, 101L, 102L, 100.0, "ONLINE", "SUCCESS", LocalDateTime.now(), "Test");

        transferRequestBody = Map.of(
                "senderAccountNumber", "SENDER-ACC",
                "receiverAccountNumber", "RECEIVER-ACC",
                "amount", 100.0,
                "pin", "1234"
        );
    }

    // Tests for performTransfer

    @Test
    void testPerformTransfer_Success_UserOwnsAccount() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.isAccountOwnedByCustomer("SENDER-ACC", 1L)).thenReturn(true);
        when(service.transferMoney(anyString(), anyString(), anyDouble(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testTransaction));
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM");

        Response response = controller.performTransfer(requestContext, transferRequestBody);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Transfer successful", entity.get("message"));
        assertNotNull(entity.get("transaction"));

        verify(service).isAccountOwnedByCustomer("SENDER-ACC", 1L);
        verify(service).transferMoney("SENDER-ACC", "RECEIVER-ACC", 100.0, "1234", "Money Transfer", "ONLINE");
    }

    @Test
    void testPerformTransfer_Forbidden_UserDoesNotOwnAccount() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.isAccountOwnedByCustomer("SENDER-ACC", 1L)).thenReturn(false);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class, () -> {
            controller.performTransfer(requestContext, transferRequestBody);
        });

        assertEquals("You can only transfer from your own account.", ex.getMessage());

        verify(service).isAccountOwnedByCustomer("SENDER-ACC", 1L);
        verify(service, never()).transferMoney(anyString(), anyString(), anyDouble(), anyString(), anyString(), anyString());
    }

    // Tests for getTransactionsByAccountCSV

    @Test
    void testGetTransactionsByAccountCSV_UserOwnsAccount_Success() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.isAccountOwnedByCustomer("MY-ACC-123", 1L)).thenReturn(true);
        when(service.getTransactionsByAccountNumber("MY-ACC-123")).thenReturn(List.of(testTransaction));
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM"); // For toResponse()

        Response response = controller.getTransactionsByAccountCSV("MY-ACC-123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());
        verify(service).isAccountOwnedByCustomer("MY-ACC-123", 1L);
        verify(service).getTransactionsByAccountNumber("MY-ACC-123");
    }

    @Test
    void testGetTransactionsByAccountCSV_UserOthersAccount_Forbidden() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.isAccountOwnedByCustomer("OTHER-ACC-456", 1L)).thenReturn(false);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class, () -> {
            controller.getTransactionsByAccountCSV("OTHER-ACC-456");
        });

        assertEquals("You do not have permission to view these transactions.", ex.getMessage());

        verify(service).isAccountOwnedByCustomer("OTHER-ACC-456", 1L);
        verify(service, never()).getTransactionsByAccountNumber(anyString());
    }

    @Test
    void testGetTransactionsByAccountCSV_AdminOthersAccount_Success() {
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(adminCustomer);
        when(service.getTransactionsByAccountNumber("OTHER-ACC-456")).thenReturn(List.of(testTransaction));
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM"); // For toResponse()

        Response response = controller.getTransactionsByAccountCSV("OTHER-ACC-456");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());

        verify(service, never()).isAccountOwnedByCustomer(anyString(), anyLong());
        verify(service).getTransactionsByAccountNumber("OTHER-ACC-456");
    }


    @Test
    void testGetTransactionById_Success() {
        when(service.getTransactionById(100L)).thenReturn(Optional.of(testTransaction));
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM"); // For toResponse()

        Response response = controller.getTransactionById(100L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Transaction found successfully", entity.get("message"));
        assertTrue(entity.get("transaction") instanceof TransactionResponse);
    }

    @Test
    void testGetTransactionById_NotFound() {
        when(service.getTransactionById(100L)).thenReturn(Optional.empty());

        TransactionNotFoundException ex = assertThrows(TransactionNotFoundException.class, () -> {
            controller.getTransactionById(100L);
        });
        assertEquals("Transaction not found for ID: 100", ex.getMessage());
    }

    @Test
    void testGetAllTransactions_Success() {
        List<Transaction> txList = Collections.singletonList(testTransaction);

        when(service.getAllTransactions()).thenReturn(txList);
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM");
        Response response = controller.getAllTransactions();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("All transactions fetched successfully", entity.get("message"));
        assertEquals(1, ((List<?>) entity.get("transactions")).size());
    }
}

