package org.bank.repositoryImpl;

import org.bank.config.DBConfig;
import org.bank.model.Account;
import org.bank.repository.AccountRepository;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl implements AccountRepository {

    @Override
    public Optional<Account> addAccount(Account account) {
        String sql = "INSERT INTO accounts (customerId, balance, accountType, accountName, accountNumber, status, ifscCode) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, account.getCustomerId());
            stmt.setDouble(2, account.getBalance());
            stmt.setString(3, account.getAccountType());
            stmt.setString(4, account.getAccountName());
            stmt.setString(5, account.getAccountNumber());
            stmt.setString(6, account.getStatus());
            stmt.setString(7, account.getIfscCode());

            int affectedRows = stmt.executeUpdate();

            if(affectedRows == 0){
                return  null;
            }

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    long accountId = rs.getLong(1);
                    account.setAccountId(accountId);
                    return findByAccountNumber(account.getAccountNumber());
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE accountNumber = ?";

        try (Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapToAccount(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Account> findAll() {
        String sql = "SELECT * FROM accounts";

        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapToAccount(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return accounts;
    }

    @Override
    public  boolean updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE accountNumber = ?";

        try(Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return  false;
        }
    }


    @Override
    public boolean updateAccountDetails(Account account) {
        String sql = "UPDATE accounts SET accountType = ?, accountName = ?, status = ?, ifscCode = ? WHERE accountNumber = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountType());
            stmt.setString(2, account.getAccountName());
            stmt.setString(3, account.getStatus());
            stmt.setString(4, account.getIfscCode());
            stmt.setString(5, account.getAccountNumber());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
           handleSQLException(e);
            return false;
        }
    }

    @Override
    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE accountNumber = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    private Account mapToAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("accountId"),
                rs.getLong("customerId"),
                rs.getObject("createdAt", LocalDateTime.class),
                rs.getObject("modifiedAt", LocalDateTime.class),
                rs.getDouble("balance"),
                rs.getString("accountType"),
                rs.getString("accountName"),
                rs.getString("accountNumber"),
                rs.getString("status"),
                rs.getString("ifscCode")
        );
    }

    private void handleSQLException(SQLException e) {
        // FK violation
        if ("23000".equals(e.getSQLState()) && e.getErrorCode() == 1452) {
            throw new RuntimeException("Customer does not exist. Cannot create account for customerId.");
        }

        String errorMessage = "Database error: " + e.getMessage()
                + " [SQLState=" + e.getSQLState()
                + ", ErrorCode=" + e.getErrorCode() + "]";
        throw new RuntimeException(errorMessage, e);
    }

    // New methods for Transaction operation with Connection parameter

    @Override
    public Optional<Account> findByAccountNumber(Connection conn, String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE accountNumber = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToAccount(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updateBalance(Connection conn, String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE accountNumber = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<String> findAccountNumberById(long accountId) {
        String sql = "SELECT accountNumber FROM accounts WHERE accountId = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getString("accountNumber"));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return Optional.empty();
    }
}
