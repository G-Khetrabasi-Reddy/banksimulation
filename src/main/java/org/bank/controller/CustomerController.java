package org.bank.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bank.exception.InvalidCustomerDataException;
import org.bank.model.Customer;
import org.bank.service.CustomerService;
import org.bank.serviceImpl.CustomerServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {

    private final CustomerService service;

    //Default Constructor (for Runtime)
    public CustomerController(){
        this.service = new CustomerServiceImpl();
    }
    //Overloaded constructor (for tests / mocking)
    public CustomerController(CustomerService service){
        this.service = service;
    }

    // POST -> onboard a new customer
    @POST
    @Path("/add")
    public Response addCustomer(Customer customer) {
        Optional<Customer> savedCustomer = service.addCustomer(customer);
        return Response.status(Response.Status.CREATED).entity(Map.of(
                "message", "Customer added successfully!",
                "customer", savedCustomer
        )).build();
    }

    // GET -> fetch customer by ID (ID from JSON body)
    @GET
    @Path("/getbyid")
    public Response getCustomerById(Map<String, Object> request) {
        Object idObj = request.get("customerId");
        if(idObj == null){
            throw new InvalidCustomerDataException("customerId is required");
        }

        long customerId = ((Number) request.get("customerId")).longValue();
        Optional<Customer> fetchedCustomer = service.findById(customerId);

        return Response.ok(Map.of(
                "message", "Customer fetched successfully",
                "customer", fetchedCustomer
        )).build();
    }


    // GET -> fetch all customers
    @GET
    @Path("/all")
    public Response getAllCustomers() {
        List<Customer> customers = service.findAll();
        return Response.ok(Map.of(
                "message", "Customers fetched successfully",
                "customers",customers
        )).build();
    }

    // PUT -> update customer details
    @PUT
    @Path("/update")
    public Response updateCustomer(Customer customer) {
        boolean result = service.updateCustomer(customer);
        return  Response.ok(Map.of(
                "message", "Customer updated successfully",
                "updated", result
        )).build();
    }

    // DELETE -> remove customer (ID from JSON body)
    @DELETE
    @Path("/delete")
    public Response deleteCustomer(Map<String, Object> request) {
        long customerId = ((Number) request.get("customerId")).longValue();
        boolean result = service.deleteCustomer(customerId);

        return Response.ok(Map.of(
                "message", "Customer deleted successfully",
                "deleted", result
        )).build();
    }

}