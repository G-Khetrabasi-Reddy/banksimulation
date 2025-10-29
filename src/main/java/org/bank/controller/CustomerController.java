package org.bank.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.bank.config.AuthenticationFilter;
import org.bank.exception.InvalidCustomerDataException;
import org.bank.exception.CustomerNotFoundException;
import org.bank.model.Customer;
import org.bank.service.CustomerService;
import org.bank.serviceImpl.CustomerServiceImpl;

import java.util.List;
import java.util.Map;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {

    private final CustomerService service;

    public CustomerController(){
        this.service = new CustomerServiceImpl();
    }

    public CustomerController(CustomerService service){
        this.service = service;
    }

    // GET customer by ID
    @GET
    @Path("/getbyid")
    public Response getCustomerById(@QueryParam("customerId") Long customerId) {
        if(customerId == null){
            throw new InvalidCustomerDataException("customerId is required");
        }

        Customer customerObj = service.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with ID: " + customerId)
        );

        return Response.ok(Map.of(
                "message", "Customer fetched successfully",
                "customer", customerObj
        )).build();
    }

    // GET all customers
    @GET
    @Path("/all")
    public Response getAllCustomers() {
        List<Customer> customers = service.findAll();
        return Response.ok(Map.of(
                "message", "Customers fetched successfully",
                "customers", customers
        )).build();
    }

    // PUT customer details (ownership enforced)
    @PUT
    @Path("/update")
    public Response updateCustomer(@Context ContainerRequestContext requestContext, Customer customer) {
        Customer loggedInCustomer = (Customer) requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY);

        if (!loggedInCustomer.getRole().equalsIgnoreCase("ADMIN") &&
                loggedInCustomer.getCustomerId() != (customer.getCustomerId())) {
            throw new InvalidCustomerDataException("You can only update your own profile.");
        }

        boolean result = service.updateCustomer(customer);
        return Response.ok(Map.of(
                "message", "Customer updated successfully",
                "updated", result
        )).build();
    }
}
