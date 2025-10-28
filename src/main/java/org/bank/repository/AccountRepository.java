package org.bank.repository;

import org.bank.model.Account;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> addAccount(Account account);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAll();
    boolean updateAccountDetails(Account account);
    List<Account> findByCustomerId(long accountId);

    // New methods for Transaction operation with Connectio parameter
    Optional<Account> findByAccountNumber(Connection conn, String accountNumber) throws SQLException;
    boolean updateBalance(Connection conn, String accountNumber, double newBalance) throws SQLException;
    Optional<String> findAccountNumberById(long accountId);
}
