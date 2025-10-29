package org.bank.service;

import org.bank.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Optional<Customer> addCustomer(Customer customer);
    Optional<Customer> findById(long customerId);
    List<Customer> findAll();
    boolean updateCustomer(Customer customer);

    Optional<Customer> findByEmail(String email);
}
