package org.bank.serviceImpl;

import org.bank.exception.*;
import org.bank.model.Account;
import org.bank.repository.AccountRepository;
import org.bank.repository.CustomerRepository;
import org.bank.repositoryImpl.AccountRepositoryImpl;
import org.bank.repositoryImpl.CustomerRepositoryImpl;
import org.bank.service.AccountService;

import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final CustomerRepository customerRepo;

    public AccountServiceImpl(){
        accountRepo = new AccountRepositoryImpl();
        customerRepo = new CustomerRepositoryImpl();
    }

    public AccountServiceImpl(AccountRepository accountRepo, CustomerRepository customerRepo){
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
    }


    @Override
    public Account openAccount(Account account) {
        // Validations
        if (account == null) throw new InvalidTransactionException("Account cannot be null.");
        if (customerRepo.findById(account.getCustomerId()).isEmpty()) { throw new InvalidTransactionException("Invalid customerId: " + account.getCustomerId()); }
        if (account.getBalance() < 0) throw new InvalidTransactionException("Initial balance cannot be negative.");
        if (account.getAccountType() == null || account.getAccountType().isBlank()) throw new InvalidTransactionException("Account type cannot be null or empty.");
        if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) throw new InvalidTransactionException("Account number cannot be null or empty.");
        if (account.getIfscCode() == null || !account.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) throw new InvalidTransactionException("Invalid IFSC code format.");
        if (account.getStatus() == null || account.getStatus().isBlank()) account.setStatus("ACTIVE"); // default

        if (accountRepo.findByAccountNumber(account.getAccountNumber()).isPresent()) {
            throw new DuplicateAccountException("Account with Number " + account.getAccountNumber() + " already exists.");
        }

        return accountRepo.addAccount(account)
                .orElseThrow(() -> new RuntimeException("Failed to create account. Please try again."));
    }

    @Override
    public void closeAccount(String accountNumber) {
        Account existing = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number " + accountNumber + " not found."));

        // Validation
        if ("CLOSED".equalsIgnoreCase(existing.getStatus())) {
            throw new AccountAlreadyClosedException("Account is already closed.");
        }

        existing.setStatus("CLOSED");
        boolean update = accountRepo.updateAccountDetails(existing);
        if(!update) throw new RuntimeException("Failed to close account " + accountNumber);
    }

    @Override
    public void deposit(String accountNumber, double amount) {
        // Validation
        if (amount <= 0) throw new InvalidTransactionException("Deposit amount must be greater than 0.");

        Account existing = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for deposit."));

        if ("CLOSED".equalsIgnoreCase(existing.getStatus())) throw new AccountAlreadyClosedException("Cannot deposit into a closed account.");

        double newBalance = existing.getBalance() + amount;
        boolean update = accountRepo.updateBalance(accountNumber, newBalance);
        if(!update) throw new RuntimeException("Deposit failed for account " + accountNumber);
    }



    @Override
    public void withdraw(String accountNumber, double amount) {
        // Validation
        if (amount <= 0) throw new InvalidTransactionException("Withdrawal amount must be greater than 0.");

        Account existing = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for withdrawal."));

        if ("CLOSED".equalsIgnoreCase(existing.getStatus())) throw new AccountAlreadyClosedException("Cannot withdraw from a closed account.");
        if (existing.getBalance() < amount) throw new InsufficientBalanceException("Insufficient balance.");

        double newBalance = existing.getBalance() - amount;
        boolean update = accountRepo.updateBalance(accountNumber, newBalance);
        if(!update) throw new RuntimeException("Withdrawal failed for account " + accountNumber);
    }



    @Override
    public Account getAccountDetails(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number " + accountNumber + " not found."));
    }


    @Override
    public double getAccountBalance(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number " + accountNumber + " not found."));
        return account.getBalance();
    }

    @Override
    public List<Account> getAllAccounts() { return accountRepo.findAll(); }

    @Override
    public void deleteAccount(String accountNumber){
        Account existing = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number " + accountNumber + " not found."));

        if (!"CLOSED".equalsIgnoreCase(existing.getStatus())) {
            throw new InvalidTransactionException("Cannot delete an active account. Close it first.");
        }

        boolean delete = accountRepo.deleteAccount(accountNumber);
        if(!delete) throw new RuntimeException("Failed to delete account " + accountNumber);
    }
}
