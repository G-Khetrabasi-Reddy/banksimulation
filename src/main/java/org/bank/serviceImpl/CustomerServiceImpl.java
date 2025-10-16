package org.bank.serviceImpl;

import org.bank.exception.CustomerNotFoundException;
import org.bank.exception.DuplicateCustomerException;
import org.bank.exception.InvalidCustomerDataException;
import org.bank.model.Customer;
import org.bank.repository.CustomerRepository;
import org.bank.repositoryImpl.CustomerRepositoryImpl;
import org.bank.service.CustomerService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepo;

    public CustomerServiceImpl(){
        customerRepo = new CustomerRepositoryImpl();
    }

    // Parameterized constructor (for testing or custom injection)
    public CustomerServiceImpl(CustomerRepository customerRepo){
        this.customerRepo = customerRepo;
    }

    // Validation Regex
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z .]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern AADHAR_PATTERN = Pattern.compile("^[2-9][0-9]{11}$");

    @Override
    public Optional<Customer> addCustomer(Customer customer) {
        validateCustomer(customer);
        Optional<Customer> saved = customerRepo.addCustomer(customer);
        if(saved.isEmpty()){
            throw new DuplicateCustomerException("Failed to add customer. Possibly duplicate Aadhar: " + customer.getAadharNumber());
        }
        return saved;
    }

    @Override
    public Optional<Customer> findById(long customerId) {
        Optional<Customer> customer = customerRepo.findById(customerId);
        if(customer.isEmpty()){
            throw new CustomerNotFoundException("Customer not found with ID "+ customerId);
        }
        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepo.findAll();
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        validateCustomer(customer);
        boolean updated = customerRepo.updateCustomer(customer);
        if(!updated){
            throw new CustomerNotFoundException("Cannot update. Customer not found with ID " + customer.getCustomerId());
        }
        return true;
    }

    @Override
    public boolean deleteCustomer(long customerId) {
        boolean deleted =  customerRepo.deleteCustomer(customerId);
        if(!deleted){
            throw new CustomerNotFoundException("Cannot delete. Customer not found with ID " + customerId);
        }
        return true;
    }

    //Validation methods
    private void validateCustomer(Customer customer) {
        if (!isValidName(customer.getName())) {
            throw new InvalidCustomerDataException("Invalid customer name: " + customer.getName());
        }
        if (!isValidPhone(customer.getPhoneNumber())) {
            throw new InvalidCustomerDataException("Invalid phone number: " + customer.getPhoneNumber());
        }
        if (isNotBlank(customer.getEmail())) {
            throw new InvalidCustomerDataException("Email cannot be empty");
        }
        if (!isValidAadhar(customer.getAadharNumber())) {
            throw new InvalidCustomerDataException("Invalid Aadhar number: " + customer.getAadharNumber());
        }
        if (isNotBlank(customer.getAddress())) {
            throw new InvalidCustomerDataException("Address cannot be empty");
        }
    }

    private boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    private boolean isValidAadhar(String aadhar){
        return aadhar != null && AADHAR_PATTERN.matcher(aadhar).matches();
    }

    private boolean isNotBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
