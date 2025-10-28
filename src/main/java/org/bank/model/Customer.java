package org.bank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class Customer {

    private long customerId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private String customerPin;
    private String aadharNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // can be set in request JSON but never sent back
    private String password;

    private String role;

    public Customer() {}

    public Customer(long customerId, String name, String phoneNumber, String email,
                    String address, String customerPin, String aadharNumber,
                    LocalDate dob, String status, String password, String role) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.customerPin = customerPin;
        this.aadharNumber = aadharNumber;
        this.dob = dob;
        this.status = status;
        this.password = password;
        this.role = role;
    }

    // --- GETTERS ---
    public long getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getCustomerPin() { return customerPin; }
    public String getAadharNumber() { return aadharNumber; }
    public LocalDate getDob() { return dob; }
    public String getStatus() { return status; }

    @JsonIgnore // prevents password from being exposed in API responses
    public String getPassword() { return password; }

    public String getRole() { return role; }

    // --- SETTERS ---
    public void setCustomerId(long customerId) { this.customerId = customerId; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setCustomerPin(String customerPin) { this.customerPin = customerPin; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setStatus(String status) { this.status = status; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }

    // --- toString() ---
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", customerPin='" + customerPin + '\'' +
                ", aadharNumber='" + aadharNumber + '\'' +
                ", dob=" + dob +
                ", status='" + status + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}