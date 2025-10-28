package org.bank.config;

import org.bank.model.Customer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// VERY BASIC - NOT FOR PRODUCTION
public class SessionManager {

    // Thread-safe map to store sessionId → Customer
    private static final Map<String, Customer> activeSessions = new ConcurrentHashMap<>();

    // Create a new session for a customer
    public static String createSession(Customer customer) {
        String sessionId = UUID.randomUUID().toString();  // unique session id
        activeSessions.put(sessionId, customer);
        System.out.println("Session created: " + sessionId + " for user: " + customer.getEmail());
        return sessionId;
    }

    // Retrieve the Customer for a given sessionId
    public static Customer getCustomer(String sessionId) {
        return activeSessions.get(sessionId);
    }

    // Invalidate a session
    public static void invalidateSession(String sessionId) {
        System.out.println("Invalidating session: " + sessionId);
        activeSessions.remove(sessionId);
    }
}
