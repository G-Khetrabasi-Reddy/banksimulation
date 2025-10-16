package org.bank.service;

public interface NotificationService {
    void emailAlert(String senderEmail, String senderName,
                    String receiverEmail, String receiverName,
                    String senderAccount, String receiverAccount, double amount);
}
