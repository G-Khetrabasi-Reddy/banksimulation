package org.bank.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Transaction {

    private long transactionId;
    private long senderAccountId;
    private long receiverAccountId;
    private Double amount;
    private String transactionMode;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactionTime;
    private String description;

    public Transaction() {}

    public Transaction(long transactionId, long senderAccountId, long receiverAccountId, Double amount,
                       String transactionMode, String status, LocalDateTime transactionTime, String description) {
        this.transactionId = transactionId;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.transactionMode = transactionMode;
        this.status = status;
        this.transactionTime = transactionTime;
        this.description = description;
    }

    // --- Getters and Setters ---

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public long getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(long senderAccountId) { this.senderAccountId = senderAccountId; }

    public long getReceiverAccountId() { return receiverAccountId; }
    public void setReceiverAccountId(long receiverAccountId) { this.receiverAccountId = receiverAccountId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getTransactionMode() { return transactionMode; }
    public void setTransactionMode(String transactionMode) { this.transactionMode = transactionMode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalDateTime transactionTime) { this.transactionTime = transactionTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", senderAccountId=" + senderAccountId +
                ", receiverAccountId=" + receiverAccountId +
                ", amount=" + amount +
                ", transactionMode='" + transactionMode + '\'' +
                ", status='" + status + '\'' +
                ", transactionTime=" + transactionTime +
                ", description='" + description + '\'' +
                '}';
    }
}

