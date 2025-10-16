package org.bank.repositoryImpl;

import org.bank.config.DBConfig;
import org.bank.model.Transaction;
import org.bank.repository.TransactionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepositoryImpl implements TransactionRepository {

    @Override
    public Optional<Transaction> saveTransaction(Connection conn, Transaction transaction) throws SQLException {
        transaction.setStatus("SUCCESS");
        String sql = "INSERT INTO transactions (senderAccountId, receiverAccountId, amount, transactionMode, status, description, transactionTime) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, transaction.getSenderAccountId());
            stmt.setLong(2, transaction.getReceiverAccountId());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getTransactionMode());
            stmt.setString(5, transaction.getStatus());
            stmt.setString(6, transaction.getDescription());
            stmt.setObject(7, transaction.getTransactionTime());

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) return Optional.empty();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long transactionId = rs.getLong(1);
                    transaction.setTransactionId(transactionId);
                }
            }
        }
        return Optional.of(transaction);
    }

    @Override
    public Optional<Transaction> findByTransactionId(long transactionId) {
        String sql = "SELECT * FROM transactions WHERE transactionId = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapToTransaction(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return transactions;
    }


    @Override
    public List<Transaction> findByAccountId(long accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE senderAccountId = ? OR receiverAccountId = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);
            stmt.setLong(2, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return transactions;
    }

    private Transaction mapToTransaction(ResultSet rs) throws SQLException {

        return new Transaction(
            rs.getLong("transactionId"),
            rs.getLong("senderAccountId"),
            rs.getLong("receiverAccountId"),
            rs.getDouble("amount"),
            rs.getString("transactionMode"),
            rs.getString("status"),
            rs.getObject("transactionTime", LocalDateTime.class),
            rs.getString("description")
        );
    }

    private void handleSQLException(SQLException e) {

        String errorMessage = "Database error: " + e.getMessage()
                + " [SQLState=" + e.getSQLState()
                + ", ErrorCode=" + e.getErrorCode() + "]";
        throw new RuntimeException(errorMessage, e);
    }


}

