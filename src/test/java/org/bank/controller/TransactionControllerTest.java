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

/**
 * Unit tests for the TransactionController.
 * This test uses Reflection to manually inject the @Context ContainerRequestContext field,
 * which is necessary to test the authentication-aware endpoints.
 * Stubbing for the context is done in individual tests to avoid UnnecessaryStubbingException.
 */
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
        // Instantiate the controller with the mocked service
        controller = new TransactionController(service);

        // --- Use Reflection to inject the @Context field ---
        try {
            Field contextField = TransactionController.class.getDeclaredField("requestContext");
            contextField.setAccessible(true);
            contextField.set(controller, requestContext);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to inject mock requestContext via reflection: " + e.getMessage());
        }
        // --- End of Reflection injection ---

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

    // --- Tests for performTransfer (requires auth) ---

    @Test
    void testPerformTransfer_Success_UserOwnsAccount() {
        // Arrange
        // Stub the authenticated user
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        // Stub the ownership check
        when(service.isAccountOwnedByCustomer("SENDER-ACC", 1L)).thenReturn(true);
        // Stub the successful transfer
        when(service.transferMoney(anyString(), anyString(), anyDouble(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(testTransaction));
        // Stub the helper method used by toResponse()
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM");

        // Act
        Response response = controller.performTransfer(requestContext, transferRequestBody);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Transfer successful", entity.get("message"));
        assertNotNull(entity.get("transaction"));

        // Verify the security check and service method were called
        verify(service).isAccountOwnedByCustomer("SENDER-ACC", 1L);
        verify(service).transferMoney("SENDER-ACC", "RECEIVER-ACC", 100.0, "1234", "Money Transfer", "ONLINE");
    }

    @Test
    void testPerformTransfer_Forbidden_UserDoesNotOwnAccount() {
        // Arrange
        // Stub the authenticated user
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        // Stub the ownership check to fail
        when(service.isAccountOwnedByCustomer("SENDER-ACC", 1L)).thenReturn(false);

        // Act & Assert
        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class, () -> {
            controller.performTransfer(requestContext, transferRequestBody);
        });

        assertEquals("You can only transfer from your own account.", ex.getMessage());

        // Verify the security check was performed
        verify(service).isAccountOwnedByCustomer("SENDER-ACC", 1L);
        // Verify the actual transfer was NEVER called
        verify(service, never()).transferMoney(anyString(), anyString(), anyDouble(), anyString(), anyString(), anyString());
    }

    // --- Tests for getTransactionsByAccountCSV (requires auth) ---

    @Test
    void testGetTransactionsByAccountCSV_UserOwnsAccount_Success() {
        // Arrange
        // Stub the authenticated user
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.isAccountOwnedByCustomer("MY-ACC-123", 1L)).thenReturn(true);
        when(service.getTransactionsByAccountNumber("MY-ACC-123")).thenReturn(List.of(testTransaction));
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM"); // For toResponse()

        // Act
        Response response = controller.getTransactionsByAccountCSV("MY-ACC-123");

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());
        verify(service).isAccountOwnedByCustomer("MY-ACC-123", 1L);
        verify(service).getTransactionsByAccountNumber("MY-ACC-123");
    }

    @Test
    void testGetTransactionsByAccountCSV_UserOthersAccount_Forbidden() {
        // Arrange
        // Stub the authenticated user
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(userCustomer);
        when(service.isAccountOwnedByCustomer("OTHER-ACC-456", 1L)).thenReturn(false);

        // Act & Assert
        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class, () -> {
            controller.getTransactionsByAccountCSV("OTHER-ACC-456");
        });

        assertEquals("You do not have permission to view these transactions.", ex.getMessage());

        // Verify the security check was performed
        verify(service).isAccountOwnedByCustomer("OTHER-ACC-456", 1L);
        // Verify the service was never called to get data
        verify(service, never()).getTransactionsByAccountNumber(anyString());
    }

    @Test
    void testGetTransactionsByAccountCSV_AdminOthersAccount_Success() {
        // Arrange
        // Stub the authenticated user as ADMIN
        when(requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY)).thenReturn(adminCustomer);
        when(service.getTransactionsByAccountNumber("OTHER-ACC-456")).thenReturn(List.of(testTransaction));
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM"); // For toResponse()

        // Act
        Response response = controller.getTransactionsByAccountCSV("OTHER-ACC-456");

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());

        // Verify the security check was (correctly) skipped for the ADMIN
        verify(service, never()).isAccountOwnedByCustomer(anyString(), anyLong());
        // Verify the data was fetched
        verify(service).getTransactionsByAccountNumber("OTHER-ACC-456");
    }

    // --- Tests for non-auth endpoints ---

    @Test
    void testGetTransactionById_Success() {
        // Arrange
        when(service.getTransactionById(100L)).thenReturn(Optional.of(testTransaction));
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM"); // For toResponse()

        // Act
        Response response = controller.getTransactionById(100L);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("Transaction found successfully", entity.get("message"));
        assertTrue(entity.get("transaction") instanceof TransactionResponse);
    }

    @Test
    void testGetTransactionById_NotFound() {
        // Arrange
        when(service.getTransactionById(100L)).thenReturn(Optional.empty());
        // No need to stub getAccountNumberById, as toResponse() will not be called

        // Act & Assert
        TransactionNotFoundException ex = assertThrows(TransactionNotFoundException.class, () -> {
            controller.getTransactionById(100L);
        });
        assertEquals("Transaction not found for ID: 100", ex.getMessage());
    }

    @Test
    void testGetAllTransactions_Success() {
        // Arrange
        List<Transaction> txList = Collections.singletonList(testTransaction);
        when(service.getAllTransactions()).thenReturn(txList);
        when(service.getAccountNumberById(anyLong())).thenReturn("ACC-NUM"); // For toResponse()

        // Act
        Response response = controller.getAllTransactions();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertEquals("All transactions fetched successfully", entity.get("message"));
        assertEquals(1, ((List<?>) entity.get("transactions")).size());
    }
}

