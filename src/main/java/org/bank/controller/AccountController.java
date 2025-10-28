package org.bank.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.bank.config.AuthenticationFilter;
import org.bank.exception.InvalidTransactionException;
import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.service.AccountService;
import org.bank.serviceImpl.AccountServiceImpl;

import java.util.List;
import java.util.Map;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountController {

    @Context
    private ContainerRequestContext requestContext;

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
    public Response closeAccount(Map<String, String> request) {
        String accountNumber = request.get("accountNumber");

        Customer loggedInCustomer = (Customer) requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY);
        if (!service.isAccountOwnedByCustomer(accountNumber, loggedInCustomer.getCustomerId())) {
            throw new InvalidTransactionException("You do not have permission to close this account.");
        }

        if (accountNumber == null || accountNumber.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "Account number must be provided in JSON body"
            )).build();
        }

        service.closeAccount(accountNumber);
        return Response.ok(Map.of("message", "Account closed successfully")).build();
    }

    @GET
    @Path("/details")
    public Response getAccountDetails(@QueryParam("accountNumber") String accountNumber) {

        if (accountNumber == null || accountNumber.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "Account number must be provided as query param"
            )).build();
        }

        Account account = service.getAccountDetails(accountNumber);
        return Response.ok(account).build();
    }

    @GET
    @Path("/my-accounts")
    public Response getMyAccounts(@Context ContainerRequestContext requestContext) {
        Customer loggedInCustomer = (Customer) requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY);
        List<Account> accounts = service.getAccountsByCustomerId(loggedInCustomer.getCustomerId());
        return Response.ok(Map.of(
                "message", "Accounts fetched successfully",
                "accounts", accounts
        )).build();
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
    public Response checkBalance(@QueryParam("accountNumber") String accountNumber){

        if(accountNumber ==null || accountNumber.isBlank()){
            return  Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "accountNumber query param is required"
            )).build();
        }

        double balance = service.getAccountBalance(accountNumber);

        return  Response.ok(Map.of(
                "accountNumber", accountNumber,
                "balance", balance
        )).build();
    }

}