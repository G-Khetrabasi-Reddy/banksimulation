package org.bank.service;

import org.bank.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Optional<Customer> addCustomer(Customer customer);        // Register new customer
    Optional<Customer> findById(long customerId);             // Search customer by ID
    List<Customer> findAll();                                 // List all customers
    boolean updateCustomer(Customer customer);                // Update email, phone, status etc.

    // 🔹 New method for login/authentication
    Optional<Customer> findByEmail(String email);
}
