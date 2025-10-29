import api from './axiosConfig';

//Get Customer by ID
export const getCustomerById = async (customerId) => {
  console.log("API: getCustomerById", customerId);
  const response = await api.get('/customer/getbyid', {
    params: { customerId }
  });
  return response.data;
};

//Get All Customers
export const getAllCustomers = async () => {
  console.log("API: getAllCustomers");
  const response = await api.get('/customer/all');
  return response.data;
};

// Update Customer
export const updateCustomer = async (customer) => {
  console.log("API: updateCustomer", customer);
  const response = await api.put('/customer/update', customer);
  return response.data;
};
