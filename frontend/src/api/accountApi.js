// src/api/accountApi.js
import api from './axiosConfig'; // Import the configured axios instance

// --- MOCK DATABASE IS REMOVED ---

/**
 * 筐 Open a new account
 * Calls POST /api/account/open
 */
export const openAccount = async (accountData) => {
  console.log("API: openAccount", accountData);
  const response = await api.post('/account/open', accountData);
  return response.data; // e.g., { message, account }
};

/**
 * 搭 Get all accounts for the logged-in user
 * Calls GET /api/account/my-accounts
 */
export const getMyAccounts = async () => {
  console.log("API: getMyAccounts");
  const response = await api.get('/account/my-accounts');
  return response.data; // e.g., { message, accounts }
};

/**
 * 搭 Get all accounts (for Admin)
 * Calls GET /api/account/all
 */
export const getAllAccounts = async () => {
  console.log("API: getAllAccounts");
  const response = await api.get('/account/all');
  return response.data; // e.g., { message, accounts }
};

/**
 * 剥 Get account details
 * Calls GET /api/account/details?accountNumber=...
 */
export const getAccountDetails = async (accountNumber) => {
  console.log("API: getAccountDetails", accountNumber);
  const response = await api.get('/account/details', {
    params: { accountNumber }
  });
  return response.data; // e.g., Account object
};

/**
 * 腸 Get account balance
 * Calls GET /api/account/balance?accountNumber=...
 */
export const getAccountBalance = async (accountNumber) => {
  console.log("API: getAccountBalance", accountNumber);
  const response = await api.get('/account/balance', {
    params: { accountNumber }
  });
  return response.data; // e.g., { accountNumber, balance }
};

/**
 * 白 Close an account
 * Calls PUT /api/account/close
 */
export const closeAccount = async (accountNumber) => {
  console.log("API: closeAccount", accountNumber);
  // Backend expects a JSON body: { "accountNumber": "..." }
  const response = await api.put('/account/close', { accountNumber });
  return response.data; // e.g., { message }
};