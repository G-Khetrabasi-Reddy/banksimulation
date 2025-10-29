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

        // Check if Aadhar already exists
        boolean duplicateExists = customerRepo.findAll().stream()
                .anyMatch(c -> c.getAadharNumber().equals(customer.getAadharNumber())
                || c.getEmail().equalsIgnoreCase(customer.getEmail()));

        if (duplicateExists) {
            throw new DuplicateCustomerException(
                    "Customer with same Aadhar or Email already exists."
            );
        }

        Optional<Customer> saved = customerRepo.addCustomer(customer);
        if(saved.isEmpty()){
            throw new DuplicateCustomerException("Failed to add customer. Please check the input data.");
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
    public Optional<Customer> findByEmail(String email) {
        Optional<Customer> customer = customerRepo.findByEmail(email);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundException("Customer not found with email " + email);
        }
        return customer;
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
        if (customer.getDob() == null) {
            throw new InvalidCustomerDataException("Date of Birth (dob) cannot be null");
        }
        if (isNotBlank(customer.getCustomerPin()) || !customer.getCustomerPin().matches("^\\d{6}$")) {
            throw new InvalidCustomerDataException("Invalid PIN. Must be 6 digits.");
        }
        if (customer.getPassword() != null) {
            if (isNotBlank(customer.getPassword()) || customer.getPassword().length() < 6) {
                throw new InvalidCustomerDataException("Invalid Password. Must be at least 6 Digits.");
            }
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

    private boolean isNotBlank(String str) { return str == null || str.trim().isEmpty(); }
}
