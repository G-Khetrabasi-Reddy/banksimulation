package org.bank.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext; // 1. Import
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context; // 2. Import
import org.bank.config.AuthenticationFilter; // 3. Import
import org.bank.config.SessionManager;
import org.bank.model.Customer;
import org.bank.service.CustomerService;
import org.bank.serviceImpl.CustomerServiceImpl;

import java.util.Map;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private final CustomerService customerService = new CustomerServiceImpl();

    @Context
    private ContainerRequestContext requestContext;
    //  SIGNUP
    @POST
    @Path("/signup")
    public Response signup(Customer customer) {
        customer.setRole("USER");

        // Register the customer
        Optional<Customer> savedCustomerOpt = customerService.addCustomer(customer);
        Customer savedCustomer = savedCustomerOpt.orElseThrow(() ->
                new WebApplicationException("Signup failed", 400)
        );

        // Create a session
        String sessionId = SessionManager.createSession(savedCustomer);

        NewCookie cookie = new NewCookie("sessionId", sessionId, "/", null, null, NewCookie.DEFAULT_MAX_AGE, false, true);

        return Response.status(Response.Status.CREATED)
                .cookie(cookie)
                .entity(Map.of(
                        "message", "Signup successful!",
                        "user", Map.of(
                                "customerId", savedCustomer.getCustomerId(),
                                "name", savedCustomer.getName(),
                                "email", savedCustomer.getEmail(),
                                "role", savedCustomer.getRole(),
                                "phoneNumber", savedCustomer.getPhoneNumber(),
                                "address", savedCustomer.getAddress(),
                                "status", savedCustomer.getStatus(),
                                "dob", savedCustomer.getDob(),
                                "aadharNumber", savedCustomer.getAadharNumber(),
                                "customerPin", savedCustomer.getCustomerPin()
                        )
                )).build();
    }

    //  LOGIN
    @POST
    @Path("/login")
    public Response login(Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Customer customer = customerService.findByEmail(email)
                .orElseThrow(() -> new WebApplicationException("Invalid email or password", 401));

        // Password check
        if (!password.equals(customer.getPassword())) {
            throw new WebApplicationException("Invalid email or password", 401);
        }

        // Create a session
        String sessionId = SessionManager.createSession(customer);

        NewCookie cookie = new NewCookie("sessionId", sessionId, "/", null, null, NewCookie.DEFAULT_MAX_AGE, false, true);

        return Response.ok()
                .cookie(cookie)
                .entity(Map.of(
                        "message", "Login successful",
                        "user", Map.of(
                                "customerId", customer.getCustomerId(),
                                "name", customer.getName(),
                                "email", customer.getEmail(),
                                "role", customer.getRole(),
                                "phoneNumber", customer.getPhoneNumber(),
                                "address", customer.getAddress(),
                                "status", customer.getStatus(),
                                "dob", customer.getDob(),
                                "aadharNumber", customer.getAadharNumber(),
                                "customerPin", customer.getCustomerPin()
                        )
                )).build();
    }

    //  LOGOUT
    @POST
    @Path("/logout")
    public Response logout(@CookieParam("sessionId") String sessionId) {
        if (sessionId != null) {
            SessionManager.invalidateSession(sessionId);
        }

        // Expire cookie
        NewCookie expiredCookie = new NewCookie("sessionId", "", "/", null, null, 0, false, true);

        return Response.ok(Map.of("message", "Logged out successfully"))
                .cookie(expiredCookie)
                .build();
    }

    @GET
    @Path("/me")
    public Response getMe() {
        Customer loggedInCustomer = (Customer) requestContext.getProperty(AuthenticationFilter.SESSION_USER_PROPERTY);

        return Response.ok(Map.of(
                "user", Map.of(
                        "customerId", loggedInCustomer.getCustomerId(),
                        "name", loggedInCustomer.getName(),
                        "email", loggedInCustomer.getEmail(),
                        "role", loggedInCustomer.getRole(),
                        "phoneNumber", loggedInCustomer.getPhoneNumber(),
                        "address", loggedInCustomer.getAddress(),
                        "status", loggedInCustomer.getStatus(),
                        "dob", loggedInCustomer.getDob(),
                        "aadharNumber", loggedInCustomer.getAadharNumber(),
                        "customerPin", loggedInCustomer.getCustomerPin()
                )
        )).build();
    }
}
