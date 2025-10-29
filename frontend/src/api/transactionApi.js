import api from './axiosConfig';

// Calls POST /api/transaction/transfer
export const performTransfer = async (transferData) => {
  console.log("API: performTransfer", transferData);
  const response = await api.post('/transaction/transfer', transferData);
  return response.data; // e.g., { message, transaction }
};

// Calls GET /api/transaction/getById?transactionId=
export const getTransactionById = async (transactionId) => {
  console.log("API: getTransactionById", transactionId);
  const response = await api.get('/transaction/getById', {
    params: { transactionId }
  });
  return response.data;
};

//Calls GET /api/transaction/getAll
export const getAllTransactions = async () => {
  console.log("API: getAllTransactions");
  const response = await api.get('/transaction/getAll');
  return response.data;
};

//Calls GET /api/transaction/getByAccount/csv?accountNumber=...
export const downloadTransactionsCSV = async (accountNumber) => {
  console.log("API: downloadTransactionsCSV", accountNumber);

  try {
    const response = await api.get('/transaction/getByAccount/csv', {
      params: { accountNumber },
      responseType: 'blob', 
    });

    const blob = new Blob([response.data], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;

    const disposition = response.headers['content-disposition'];
    let filename = `transactions_${accountNumber}.csv`;
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

    link.parentNode.removeChild(link);
    window.URL.revokeObjectURL(url);
    
  } catch (err) {
    console.error("Failed to download CSV:", err);
    throw new Error(err.response?.data?.message || "Failed to download CSV.");
  }
};