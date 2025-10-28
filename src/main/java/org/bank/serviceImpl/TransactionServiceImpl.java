package org.bank.serviceImpl;

import org.bank.config.DBConfig;
import org.bank.exception.*;
import org.bank.model.Account;
import org.bank.model.Customer;
import org.bank.model.Transaction;
import org.bank.repository.AccountRepository;
import org.bank.repository.CustomerRepository;
import org.bank.repository.TransactionRepository;
import org.bank.repositoryImpl.AccountRepositoryImpl;
import org.bank.repositoryImpl.CustomerRepositoryImpl;
import org.bank.repositoryImpl.TransactionRepositoryImpl;
import org.bank.service.NotificationService;
import org.bank.service.TransactionService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    private  final CustomerRepository customerRepo;
    private final NotificationService notificationService;

    public TransactionServiceImpl() {
        accountRepo = new AccountRepositoryImpl();
        transactionRepo = new TransactionRepositoryImpl();
        customerRepo = new CustomerRepositoryImpl();
        notificationService = new NotificationServiceImpl();
    }

    public TransactionServiceImpl(AccountRepository accountRepo, TransactionRepository transactionRepo,
                                  NotificationService notificationService, CustomerRepository customerRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.customerRepo = customerRepo;
        this.notificationService = notificationService;
    }

    @Override
    public Optional<Transaction> transferMoney(String senderAccountNumber, String receiverAccountNumber, double amount, String pin, String description, String transactionMode) {
        // Validation
        if(senderAccountNumber == null || senderAccountNumber.isBlank()) throw new InvalidTransactionException("Sender Account number can't be null or empty.");
        if(receiverAccountNumber == null || receiverAccountNumber.isBlank()) throw new InvalidTransactionException("Receiver Account number can't be null or empty.");
        if (senderAccountNumber.equals(receiverAccountNumber)) throw new InvalidTransactionException("Sender and receiver accounts cannot be the same.");
        if (amount <= 0) throw new InvalidTransactionException("Transaction amount must be positive.");
        if(pin == null || pin.isBlank()) throw new InvalidTransactionException("PIN cannot be null or empty");
        if (transactionMode == null || transactionMode.isBlank()) { transactionMode = "ONLINE"; }

        Connection conn = null;
        try {
            //Created DB Connection
            conn = DBConfig.getConnection();
            conn.setAutoCommit(false);

            //Fetch account based on AccountNUmber
            Account senderAccount = accountRepo.findByAccountNumber(conn, senderAccountNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Sender account not found."));

            Account receiverAccount = accountRepo.findByAccountNumber(conn, receiverAccountNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Receiver account not found."));

            //Fetch Customer detail
            Customer senderCustomer = customerRepo.findById(senderAccount.getCustomerId())
                    .orElseThrow( () -> new InvalidCustomerDataException("Sender customer not found"));
            Customer receiverCustomer = customerRepo.findById(receiverAccount.getCustomerId())
                    .orElseThrow( () -> new InvalidCustomerDataException("Receiver customer not found"));

            if (!"ACTIVE".equalsIgnoreCase(senderAccount.getStatus())) throw new InvalidTransactionException("Sender account is not active.");
            if (!"ACTIVE".equalsIgnoreCase(receiverAccount.getStatus())) throw new InvalidTransactionException("Receiver account is not active.");
            if(!senderCustomer.getCustomerPin().equals(pin))throw new InvalidTransactionException("Invalid PIN. Transaction is canceled.");
            if (senderAccount.getBalance() < amount) throw new InsufficientBalanceException("Insufficient balance in sender's account.");

            // Debit and Credit operation
            double newSenderBalance = senderAccount.getBalance() - amount;
            accountRepo.updateBalance(conn, senderAccountNumber, newSenderBalance);

            double newReceiverBalance = receiverAccount.getBalance() + amount;
            accountRepo.updateBalance(conn, receiverAccountNumber, newReceiverBalance);

            System.out.println("Balances updated successfully in memory.");

            //Create record for transaction table
            Transaction transaction = new Transaction();
            transaction.setSenderAccountId(senderAccount.getAccountId());
            transaction.setReceiverAccountId(receiverAccount.getAccountId());
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transaction.setTransactionMode(transactionMode);
            transaction.setTransactionTime(LocalDateTime.now());

            Optional<Transaction> savedTransaction = transactionRepo.saveTransaction(conn, transaction);
            System.out.println("Transaction record created successfully.");

            //Commit Transaction
            conn.commit();
            System.out.println("Transaction successful. Email notification triggered...");

            try{
                //Send mail notification to both account
                notificationService.emailAlert(
                        senderCustomer.getEmail(), senderCustomer.getName(),
                        receiverCustomer.getEmail(), receiverCustomer.getName(),
                        senderAccountNumber, receiverAccountNumber, amount
                );
            } catch (Exception e) {
                System.err.println("Warning: Transaction completed but email notification failed: " + e.getMessage());
            }

            return savedTransaction;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to SQL error.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new TransactionFailedException("Database error occurred during transaction.", e);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to application error.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Re-throw other exceptions (AccountNotFound, etc.) to be handled by the controller
            throw e;
        }
        finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); //Rest to default
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Optional<Transaction> getTransactionById(long transactionId) {
        return transactionRepo.findByTransactionId(transactionId);
    }

    @Override
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found."));

        return transactionRepo.findByAccountId(account.getAccountId());
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    @Override
    public String getAccountNumberById(long accountId) {
        return accountRepo.findAccountNumberById(accountId)
                .orElse("UNKNOWN");  // Returns "UNKNOWN" if accountId does not exist
    }

    @Override
    public boolean isAccountOwnedByCustomer(String accountNumber, long customerId) {
        return accountRepo.findByAccountNumber(accountNumber)
                .map(account -> account.getCustomerId() == customerId)  // primitive long comparison
                .orElse(false);  // account not found -> not owned
    }


}
