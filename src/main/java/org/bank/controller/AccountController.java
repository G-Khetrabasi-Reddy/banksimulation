package org.bank.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bank.exception.InvalidTransactionException;
import org.bank.model.Account;
import org.bank.service.AccountService;
import org.bank.serviceImpl.AccountServiceImpl;

import java.util.List;
import java.util.Map;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountController {

    private final AccountService service;

    public  AccountController(){
        service = new AccountServiceImpl();
    }

    // Constructor for testing
    public AccountController(AccountService service){
        this.service = service;
    }

    @POST
    @Path("/open")
    public Response openAccount(Account account) {
        Account createdAccount = service.openAccount(account);
        return Response.status(Response.Status.CREATED).entity(Map.of(
                "message", "Account opened successfully",
                "account", createdAccount
        )).build();
    }

    @PUT
    @Path("/close")
    public Response closeAccount(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "Account number must be provided in JSON body"
            )).build();
        }

        service.closeAccount(account.getAccountNumber());
        return Response.ok(Map.of("message", "Account closed successfully")).build();
    }

    @PUT
    @Path("/deposit")
    public Response deposit(Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        double amount = ((Number) request.get("amount")).doubleValue();

        if(accountNumber == null || accountNumber.isEmpty() || amount <= 0){
            throw new InvalidTransactionException("Account number and amount must be provided and amount > 0");
        }

        service.deposit(accountNumber, amount);
        double balance = service.getAccountBalance(accountNumber);

        return Response.ok(Map.of(
                "message", "Deposit successful",
                "amount", amount,
                "accountNumber", accountNumber,
                "balance", balance    // 🔹 added balance
        )).build();
    }

    @PUT
    @Path("/withdraw")
    public Response withdraw(Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        double amount = ((Number) request.get("amount")).doubleValue();

        if(accountNumber == null || accountNumber.isEmpty() || amount <= 0){
            throw new InvalidTransactionException("Account number and amount must be provided and amount > 0");
        }

        service.withdraw(accountNumber, amount);
        double balance = service.getAccountBalance(accountNumber);

        return Response.ok(Map.of(
                "message", "Withdrawal successful",
                "amount", amount,
                "accountNumber", accountNumber,
                "balance", balance    // 🔹 added balance
        )).build();

    }


    @GET
    @Path("/details")
    public Response getAccountDetails(Map<String, String> request) {
        String accountNumber = request.get("accountNumber");

        if (accountNumber == null || accountNumber.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "Account number must be provided as query param"
            )).build();
        }

        Account account = service.getAccountDetails(accountNumber);
        return Response.ok(account).build();
    }

    @GET
    @Path("/all")
    public Response getAllAccounts(){
        List<Account> accounts = service.getAllAccounts();
        return Response.ok(Map.of(
                "message", "Accounts fetched successfully",
                "accounts", accounts
        )).build();
    }

    @GET
    @Path("/balance")
    public Response checkBalance(Map<String, Object> request){
        String accountNumber =(String) request.get("accountNumber");
        double balance = service.getAccountBalance(accountNumber);

        return  Response.ok(Map.of(
                "accountNumber", accountNumber,
                "balance", balance
        )).build();
    }

    @DELETE
    @Path("/delete")
    public Response deleteAccount(Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");

        if (accountNumber == null || accountNumber.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "Account number must be provided in JSON body"
            )).build();
        }

        service.deleteAccount(accountNumber);
        return Response.ok(Map.of(
                "message", "Account deleted successfully",
                "accountNumber", accountNumber
        )).build();
    }

}