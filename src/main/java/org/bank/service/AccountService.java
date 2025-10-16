package org.bank.service;

import org.bank.model.Account;
import java.util.List;

public interface AccountService {
    Account openAccount(Account account);
    void closeAccount(String accountNumber);
    void deposit(String accountNumber, double amount);
    void withdraw(String accountNumber, double amount);
    Account getAccountDetails(String accountNumber);
    double getAccountBalance(String accountNumber);
    List<Account> getAllAccounts();
    void deleteAccount(String accountNumber);

}
