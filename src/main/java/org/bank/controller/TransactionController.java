package org.bank.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bank.dto.TransactionResponse;
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

    private final TransactionService transactionService;

    public TransactionController() {
        this.transactionService = new TransactionServiceImpl();
    }

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    @Path("/transfer")
    public Response performTransfer(Map<String, Object> request) {
        String senderAccountNumber = (String) request.get("senderAccountNumber");
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
    public Response getTransactionById(Map<String, Object> request) {
        long transactionId = ((Number) request.get("transactionId")).longValue();
        Optional<Transaction> transaction = transactionService.getTransactionById(transactionId);

        return transaction.map(t -> Response.ok(Map.of(
                                "message", "Transaction found successfully",
                                "transaction", toResponse(t)))
                        .build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Transaction not found for ID: " + transactionId))
                        .build());
    }

    @GET
    @Path("/getByAccount")
    public Response getTransactionsByAccount(Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        List<Transaction> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);

        List<TransactionResponse> responseList = transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return Response.ok(Map.of(
                "message", "Transactions fetched successfully for account " + accountNumber,
                "transactions", responseList
        )).build();
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
}
