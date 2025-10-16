package org.bank.repository;

import org.bank.model.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> saveTransaction(Connection conn, Transaction transaction) throws SQLException;
    Optional<Transaction> findByTransactionId(long transactionId);
    List<Transaction> findAll();
    List<Transaction> findByAccountId(long accountId);

}

