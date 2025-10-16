package org.bank.model;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    public Customer(){
        //empty constructor for JSON deserialization
    }

    // Constructor with ID (for existing customers from DB)
    public Customer(long customerId, String name, String phoneNumber, String email,
                    String address, String customerPin, String aadharNumber,
                    LocalDate dob, String status) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.customerPin = customerPin;
        this.aadharNumber = aadharNumber;
        this.dob = dob;
        this.status = status;
    }


    // Getters
    public long getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCustomerPin() {
        return customerPin;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCustomerPin(String customerPin) {
        this.customerPin = customerPin;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public void setDob(LocalDate dob) { this.dob = dob; }

    public void setStatus(String status) {
        this.status = status;
    }


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
                '}';
    }
}