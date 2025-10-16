package org.bank.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Account {
    private long accountId;
    private long customerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedAt;

    private double balance;
    private String accountType;
    private String accountName;
    private String accountNumber;
    private String status;
    private String ifscCode;

    public Account() {}

    public Account(long accountId, long customerId, LocalDateTime createdAt, LocalDateTime modifiedAt,
                   double balance, String accountType, String accountName, String accountNumber, String status, String ifscCode) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.balance = balance;
        this.accountType = accountType;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.status = status;
        this.ifscCode = ifscCode;
    }

    // Getters
    public long getAccountId() { return accountId; }
    public long getCustomerId() { return customerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getAccountName() { return accountName; }
    public String getAccountNumber() { return accountNumber; }
    public String getStatus() { return status; }
    public String getIfscCode() { return ifscCode; }

    //Setters
    public void setAccountId(long accountId){ this.accountId = accountId; }
    public void setCustomerId(long customerId){ this.customerId = customerId; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
    public void setModifiedAt(LocalDateTime modifiedAt){ this.modifiedAt = modifiedAt; }
    public void setBalance(double balance){ this.balance = balance; }
    public  void setAccountType(String accountType){ this.accountType = accountType; }
    public void setAccountName(String accountName){ this.accountName = accountName; }
    public void setAccountNumber(String accountNumber){ this.accountNumber = accountNumber; }
    public void setStatus(String status){ this.status = status; }
    public void setIfscCode(String ifscCode){ this.ifscCode = ifscCode; }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", customerId=" + customerId + '\'' +
                ", createAt=" + createdAt + '\'' +
                ", modifiedAt=" + modifiedAt + '\'' +
                ", balance=" + balance + '\'' +
                ", accountType=" + accountType + '\'' +
                ", accountName=" + accountName + '\'' +
                ", accountNumber=" + accountNumber + '\'' +
                ", status=" + status + '\'' +
                ",ifscCode=" + ifscCode + '\'' +
                '}';
    }
}
