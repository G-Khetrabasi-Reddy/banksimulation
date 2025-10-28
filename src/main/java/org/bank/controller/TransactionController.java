package org.bank.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bank.config.AuthenticationFilter;
import org.bank.dto.TransactionResponse;
import org.bank.exception.InvalidTransactionException;
import org.bank.exception.TransactionNotFoundException;
import org.bank.model.Customer;
import org.bank.model.Transaction;
import org.bank.service.TransactionService;
import org.bank.serviceImpl.TransactionServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionController {

    @Context
    private ContainerRequestContext requestContext;

    private final TransactionService transactionService;

    public TransactionController() {
        this.transactionService = new TransactionServiceImpl();
    }

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    @Path("/transfer")
    public Response performTransfer(@Context ContainerRequestContext requestContext, Map<String, Object> request) {
        // --- Ownership check ---
        Customer loggedInCustomer = (Customer) requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY);
        String senderAccountNumber = (String) request.get("senderAccountNumber");
        if (!transactionService.isAccountOwnedByCustomer(senderAccountNumber, loggedInCustomer.getCustomerId())) {
            throw new InvalidTransactionException("You can only transfer from your own account.");
        }

        String receiverAccountNumber = (String) request.get("receiverAccountNumber");
        double amount = ((Number) request.get("amount")).doubleValue();
        String pin = (String) request.get("pin");
        String description = (String) request.getOrDefault("description", "Money Transfer");
        String transactionMode = (String) request.getOrDefault("transactionMode", "ONLINE");


        Optional<Transaction> savedTransaction = transactionService.transferMoney(
                senderAccountNumber, receiverAccountNumber, amount, pin, description,
                transactionMode);

        TransactionResponse response = savedTransaction
                .map(this::toResponse)
                .orElse(null);

        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "message", "Transfer successful",
                        "transaction", response
                ))
                .build();
    }

    @GET
    @Path("/getById")
    public Response getTransactionById(@QueryParam("transactionId") Long transactionId) {
        if(transactionId == null){
            throw  new InvalidTransactionException("transactionId is required");
        }

        Optional<Transaction> transactionOpt = transactionService.getTransactionById(transactionId);

        // More explicit handling using orElseThrow or similar
        Transaction transaction = transactionOpt.orElseThrow(
                () -> new TransactionNotFoundException("Transaction not found for ID: " + transactionId) // Use a specific exception
        );

        return Response.ok(Map.of(
                "message", "Transaction found successfully",
                "transaction", toResponse(transaction))
        ).build();
    }

    @GET
    @Path("/getByAccount/csv")
    @Produces("text/csv") // This is for the container
    public Response getTransactionsByAccountCSV(@QueryParam("accountNumber") String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new InvalidTransactionException("Account number must be provided.");
        }

        Customer loggedInCustomer = (Customer) requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY);

        // Only allow if ADMIN or if the USER owns the account
        if (!loggedInCustomer.getRole().equalsIgnoreCase("ADMIN")) {
            if (!transactionService.isAccountOwnedByCustomer(accountNumber, loggedInCustomer.getCustomerId())) {
                throw new InvalidTransactionException("You do not have permission to view these transactions.");
            }
        }

        List<Transaction> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        List<TransactionResponse> responseList = transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return buildCSVResponse(responseList, accountNumber);
    }

    @GET
    @Path("/getAll")
    public Response getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionResponse> responseList = transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return Response.ok(Map.of(
                "message", "All transactions fetched successfully",
                "transactions", responseList
        )).build();
    }

    private TransactionResponse toResponse(Transaction t) {
        String senderAccNum = transactionService.getAccountNumberById(t.getSenderAccountId());
        String receiverAccNum = transactionService.getAccountNumberById(t.getReceiverAccountId());

        return new TransactionResponse(
                t.getTransactionId(),
                senderAccNum,
                receiverAccNum,
                t.getAmount(),
                t.getTransactionMode(),
                t.getStatus(),
                t.getTransactionTime().toString(),
                t.getDescription()
        );
    }

    private Response buildCSVResponse(List<TransactionResponse> transactions, String accountNumber) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Transaction ID,Sender Account,Receiver Account,Amount,Date,Description,Mode\n");

        for (TransactionResponse t : transactions) {
            csvBuilder.append(t.getTransactionId()).append(",")
                    .append(t.getSenderAccountNumber()).append(",")
                    .append(t.getReceiverAccountNumber()).append(",")
                    .append(t.getAmount()).append(",")
                    .append(t.getTransactionTime()).append(",")
                    .append(escapeCSV(t.getDescription())).append(",")
                    .append(t.getTransactionMode())
                    .append("\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes();

        // --- THE FIX IS HERE ---
        // We must explicitly set the "Content-Type" for the Response object
        return Response.ok(csvBytes)
                .type("text/csv") // <-- THIS LINE IS THE FIX
                .header("Content-Disposition", "attachment; filename=\"transactions_" + accountNumber + ".csv\"")
                .build();
    }

    private String escapeCSV(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"")) {
            text = text.replace("\"", "\"\"");
            return "\"" + text + "\"";
        }
        return text;
    }
}