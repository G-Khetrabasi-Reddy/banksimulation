package org.bank.repository;

import org.bank.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> addCustomer(Customer customer);
    Optional<Customer> findById(long customerId);
    List<Customer> findAll();
    boolean updateCustomer(Customer customer);

    // 🔹 New method for authentication or email lookup
    Optional<Customer> findByEmail(String email);
}
