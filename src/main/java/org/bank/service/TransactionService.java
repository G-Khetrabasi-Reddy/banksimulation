package org.bank.service;

import org.bank.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Optional<Transaction> transferMoney(String senderAccountNumber, String receiverAccountNumber, double amount,String pin, String description, String transactionMode);
    Optional<Transaction> getTransactionById(long transactionId);
    List<Transaction> getTransactionsByAccountNumber(String accountNumber);
    List<Transaction> getAllTransactions();
    String getAccountNumberById(long accountId);
    boolean isAccountOwnedByCustomer(String accountNumber, long customerId);
}

