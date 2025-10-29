import React, { createContext, useState, useEffect } from 'react';
import {
    signup as apiSignup,
    login as apiLogin,
    logout as apiLogout,
    getMe as apiGetMe
} from '../api/authApi';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const validateSession = async () => {
            try {
                // Check if the user has a valid session cookie
                const { user } = await apiGetMe();
                setUser(user);
            } catch (err) {
                console.log("No active session found.");
                setUser(null);
            } finally {
                setIsLoading(false);
            }
        };
        
        validateSession();
    }, []);

    const login = async (email, password) => {
        const { user } = await apiLogin(email, password);
        setUser(user);
    };

    const signup = async (customerData) => {
        const { user } = await apiSignup(customerData); 
        setUser(user);
        return user;
    };

    const logout = async () => {
        try {
            await apiLogout(); 
        } catch (err) {
            console.error("Error logging out from server:", err);
        } finally {
            setUser(null);
        }
    };


    const updateUser = (newUserData) => {
        console.log("MOCK: Updating user in context", newUserData);
        setUser(prevUser => ({ ...prevUser, ...newUserData }));
    };
    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout, signup, updateUser }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;