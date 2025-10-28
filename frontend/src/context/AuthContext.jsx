import React, { createContext, useState, useEffect } from 'react';
// 1. Import all our new REAL api functions
import {
    signup as apiSignup,
    login as apiLogin, // Import new login
    logout as apiLogout,
    getMe as apiGetMe // Import new session checker
} from '../api/authApi';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    // 2. We no longer need 'token' or localStorage. The cookie is king.
    //    (Remove token state)

    // 3. This useEffect is now for validating the session cookie
    useEffect(() => {
        const validateSession = async () => {
            try {
                // Check if the user has a valid session cookie
                const { user } = await apiGetMe();
                setUser(user);
            } catch (err) {
                // No valid session (e.g., 401 error)
                console.log("No active session found.");
                setUser(null);
            } finally {
                setIsLoading(false);
            }
        };
        
        validateSession();
    }, []); // Runs once on app load

    // 4. Update the login function to call the real API
    const login = async (email, password) => {
        // Remove all hard-coded mock logic
        const { user } = await apiLogin(email, password);
        setUser(user);
        // The cookie is set automatically by the browser
    };

    // 5. Update the signup function
    const signup = async (customerData) => {
        // This now calls our REAL apiSignup
        // --- THIS IS THE FIX ---
        const { user } = await apiSignup(customerData); 
        setUser(user);
        // The cookie is set automatically by the browser
        return user;
    };

    // 6. Update the logout function
    const logout = async () => {
        try {
            await apiLogout(); 
        } catch (err) {
            console.error("Error logging out from server:", err);
        } finally {
            // Clear client-side state
            setUser(null);
        }
    };

    // 7. Update the updateUser function (for ProfilePage)
    //    We remove the token/localStorage logic.
    const updateUser = (newUserData) => {
        console.log("MOCK: Updating user in context", newUserData);
        setUser(prevUser => ({ ...prevUser, ...newUserData }));
    };
    
    // 8. Remove the parseJwt function. It's no longer needed.

    // 9. Update the provided value (remove token)
    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout, signup, updateUser }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;