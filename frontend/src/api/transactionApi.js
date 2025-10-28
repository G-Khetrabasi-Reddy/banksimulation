// src/api/transactionApi.js
import api from './axiosConfig'; // 1. Import the configured axios instance

// --- MOCK DATABASE IS REMOVED ---

/**
 * Performs a money transfer.
 * Calls POST /api/transaction/transfer
 */
export const performTransfer = async (transferData) => {
  console.log("API: performTransfer", transferData);
  const response = await api.post('/transaction/transfer', transferData);
  return response.data; // e.g., { message, transaction }
};

/**
 * Gets a single transaction by its ID.
 * Calls GET /api/transaction/getById?transactionId=...
 */
export const getTransactionById = async (transactionId) => {
  console.log("API: getTransactionById", transactionId);
  const response = await api.get('/transaction/getById', {
    params: { transactionId }
  });
  return response.data; // e.g., { message, transaction }
};

/**
 * Gets all transactions (for Admin).
 * Calls GET /api/transaction/getAll
 */
export const getAllTransactions = async () => {
  console.log("API: getAllTransactions");
  const response = await api.get('/transaction/getAll');
  return response.data; // e.g., { message, transactions }
};

/**
 * Downloads a CSV of transactions for a specific account.
 * Calls GET /api/transaction/getByAccount/csv?accountNumber=...
 */
export const downloadTransactionsCSV = async (accountNumber) => {
  console.log("API: downloadTransactionsCSV", accountNumber);

  try {
    // 2. We must tell axios to expect a 'blob' (the CSV file)
    const response = await api.get('/transaction/getByAccount/csv', {
      params: { accountNumber },
      responseType: 'blob', 
    });

    // 3. The rest of this logic triggers the browser download
    const blob = new Blob([response.data], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;

    // Try to get filename from backend's 'Content-Disposition' header
    const disposition = response.headers['content-disposition'];
    let filename = `transactions_${accountNumber}.csv`; // Default
    if (disposition && disposition.indexOf('attachment') !== -1) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
      const matches = filenameRegex.exec(disposition);
      if (matches != null && matches[1]) {
        filename = matches[1].replace(/['"]/g, '');
      }
    }

    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();

    // Clean up
    link.parentNode.removeChild(link);
    window.URL.revokeObjectURL(url);
    
  } catch (err) {
    console.error("Failed to download CSV:", err);
    // Handle error - maybe the API call failed (e.g., 404, 500)
    // We need to re-throw so the component can catch it
    throw new Error(err.response?.data?.message || "Failed to download CSV.");
  }
};