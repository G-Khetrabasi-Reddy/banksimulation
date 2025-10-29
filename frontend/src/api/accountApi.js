import api from './axiosConfig';


//Open a new account
export const openAccount = async (accountData) => {
  console.log("API: openAccount", accountData);
  const response = await api.post('/account/open', accountData);
  return response.data;
};


//Get all accounts for the logged-in user
export const getMyAccounts = async () => {
  console.log("API: getMyAccounts");
  const response = await api.get('/account/my-accounts');
  return response.data;
};

// Get all accounts (for Admin)
export const getAllAccounts = async () => {
  console.log("API: getAllAccounts");
  const response = await api.get('/account/all');
  return response.data;
};

//Get account details
export const getAccountDetails = async (accountNumber) => {
  console.log("API: getAccountDetails", accountNumber);
  const response = await api.get('/account/details', {
    params: { accountNumber }
  });
  return response.data;
};

//Get account balance
export const getAccountBalance = async (accountNumber) => {
  console.log("API: getAccountBalance", accountNumber);
  const response = await api.get('/account/balance', {
    params: { accountNumber }
  });
  return response.data;
};

//Close an account
export const closeAccount = async (accountNumber) => {
  console.log("API: closeAccount", accountNumber);
  const response = await api.put('/account/close', { accountNumber });
  return response.data;
};