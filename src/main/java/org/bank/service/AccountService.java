package org.bank.service;

import org.bank.model.Account;
import java.util.List;

public interface AccountService {
    Account openAccount(Account account);
    void closeAccount(String accountNumber);
    Account getAccountDetails(String accountNumber);
    double getAccountBalance(String accountNumber);
    List<Account> getAllAccounts();
    List<Account> getAccountsByCustomerId(long customerId);
    boolean isAccountOwnedByCustomer(String accountNumber, long customerId);
}
