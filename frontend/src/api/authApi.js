// src/api/authApi.js
import api from './axiosConfig'; // 1. Import the configured axios instance

/**
 * Calls the backend login endpoint.
 * @param {string} email
 * @param {string} password
 * @returns {Promise<object>} The server response (e.g., { message, user })
 */
export const login = async (email, password) => {
  const response = await api.post('/auth/login', { email, password });
  return response.data;
};

/**
 * Calls the backend signup endpoint.
 * @param {object} customerData
 * @returns {Promise<object>} The server response (e.g., { message, user })
 */
export const signup = async (customerData) => {
  const response = await api.post('/auth/signup', customerData);
  return response.data;
};

/**
 * Calls the backend logout endpoint.
 */
export const logout = async () => {
  const response = await api.post('/auth/logout');
  return response.data;
};

/**
 * Calls the new backend /auth/me endpoint to get the
 * currently logged-in user (if a valid session cookie exists).
 */
export const getMe = async () => {
  const response = await api.get('/auth/me');
  return response.data;
};