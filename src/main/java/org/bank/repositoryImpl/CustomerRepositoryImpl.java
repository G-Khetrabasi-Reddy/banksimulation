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
    public Optional<Customer> addCustomer(Customer customer){

        String sql = "INSERT INTO customers (name, phoneNumber, email, address, customerPin, aadharNumber, dob, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

            int affectedRows = stmt.executeUpdate();

            if(affectedRows == 0){
                return Optional.empty();
            }

            //Get the generated Customer ID
            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    long generatedCustomerId = rs.getLong(1);
                    customer.setCustomerId(generatedCustomerId); // set the auto-generated ID
                    return Optional.of(customer); // return customer with ID
                }
            }

        } catch (SQLException e) {
           handleSQLException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findById(long customerId){

        String sql = "SELECT * FROM customers WHERE customerId = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of((new Customer(
                        rs.getLong("customerId"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("customerPin"),
                        rs.getString("aadharNumber"),
                        rs.getDate("dob").toLocalDate(),
                        rs.getString("status")
                )));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        return Optional.empty(); //not found
    }

    @Override
    public List<Customer> findAll(){
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getLong("customerId"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("customerPin"),
                        rs.getString("aadharNumber"),
                        rs.getDate("dob").toLocalDate(),
                        rs.getString("status")
                ));
            }
        }catch (SQLException e){
            handleSQLException(e);
        }
        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer){
        String sql = "UPDATE customers SET name=?, phoneNumber=?, email=?, address=?, customerPin=?, aadharNumber=?, dob=?, status=? WHERE customerId=?";
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
            stmt.setLong(9, customer.getCustomerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(long customerId){
        String sql = "DELETE FROM customers WHERE customerId = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }

    }

    // 🔹 Centralized Exception Handling (Approach 1)
    private void handleSQLException(SQLException e) {
        String errorMessage = "Database error: " + e.getMessage()
                + " [SQLState=" + e.getSQLState()
                + ", ErrorCode=" + e.getErrorCode() + "]";
        throw new RuntimeException(errorMessage, e);
    }
}
