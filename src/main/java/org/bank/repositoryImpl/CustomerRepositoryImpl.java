package org.bank.repositoryImpl;

import org.bank.config.DBConfig;
import org.bank.model.Customer;
import org.bank.repository.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public Optional<Customer> addCustomer(Customer customer) {

        String sql = "INSERT INTO customers (name, phoneNumber, email, address, customerPin, aadharNumber, dob, status, password, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getCustomerPin());
            stmt.setString(6, customer.getAadharNumber());
            stmt.setDate(7, Date.valueOf(customer.getDob()));
            stmt.setString(8, customer.getStatus());
            stmt.setString(9, customer.getPassword());
            stmt.setString(10, customer.getRole());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return Optional.empty();
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long generatedCustomerId = rs.getLong(1);
                    customer.setCustomerId(generatedCustomerId);
                    return Optional.of(customer);
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Customer> findById(long customerId) {
        String sql = "SELECT * FROM customers WHERE customerId = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET name=?, phoneNumber=?, email=?, address=?, customerPin=?, aadharNumber=?, dob=?, status=?, password=?, role=? WHERE customerId=?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getCustomerPin());
            stmt.setString(6, customer.getAadharNumber());
            stmt.setDate(7, Date.valueOf(customer.getDob()));
            stmt.setString(8, customer.getStatus());
            stmt.setString(9, customer.getPassword());
            stmt.setString(10, customer.getRole());
            stmt.setLong(11, customer.getCustomerId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return Optional.empty();
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getLong("customerId"),
                rs.getString("name"),
                rs.getString("phoneNumber"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("customerPin"),
                rs.getString("aadharNumber"),
                rs.getDate("dob").toLocalDate(),
                rs.getString("status"),
                rs.getString("password"),
                rs.getString("role")
        );
    }

    // Centralized Exception Handling
    private void handleSQLException(SQLException e) {
        String errorMessage = "Database error: " + e.getMessage()
                + " [SQLState=" + e.getSQLState()
                + ", ErrorCode=" + e.getErrorCode() + "]";
        throw new RuntimeException(errorMessage, e);
    }
}