package org.bank.dto;

public class TransactionResponse {
    private long transactionId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private double amount;
    private String transactionMode;
    private String status;
    private String transactionTime;
    private String description;

    public TransactionResponse(long transactionId, String senderAccountNumber, String receiverAccountNumber,
                               double amount, String transactionMode, String status, String transactionTime,
                               String description) {
        this.transactionId = transactionId;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.transactionMode = transactionMode;
        this.status = status;
        this.transactionTime = transactionTime;
        this.description = description;
    }

    // --- Getters and Setters ---
    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public String getSenderAccountNumber() { return senderAccountNumber; }
    public void setSenderAccountNumber(String senderAccountNumber) { this.senderAccountNumber = senderAccountNumber; }

    public String getReceiverAccountNumber() { return receiverAccountNumber; }
    public void setReceiverAccountNumber(String receiverAccountNumber) { this.receiverAccountNumber = receiverAccountNumber; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTransactionMode() { return transactionMode; }
    public void setTransactionMode(String transactionMode) { this.transactionMode = transactionMode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionTime() { return transactionTime; }
    public void setTransactionTime(String transactionTime) { this.transactionTime = transactionTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
