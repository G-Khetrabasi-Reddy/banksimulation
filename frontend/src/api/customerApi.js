// src/api/customerApi.js
import api from './axiosConfig'; // 1. Import the configured axios instance

// 2. We remove addCustomer, as this is handled by the authApi.signup()
//    and the Admin page doesn't have an "Add Customer" button.

/**
 * 🔍 Get Customer by ID
 * Calls GET /api/customer/getbyid?customerId=...
 */
export const getCustomerById = async (customerId) => {
  console.log("API: getCustomerById", customerId);
  const response = await api.get('/customer/getbyid', {
    params: { customerId }
  });
  return response.data; // e.g., { message, customer }
};

/**
 * 📋 Get All Customers
 * Calls GET /api/customer/all
 */
export const getAllCustomers = async () => {
  console.log("API: getAllCustomers");
  const response = await api.get('/customer/all');
  return response.data; // e.g., { message, customers }
};

/**
 * ✏️ Update Customer
 * Calls PUT /api/customer/update
 */
export const updateCustomer = async (customer) => {
  console.log("API: updateCustomer", customer);
  const response = await api.put('/customer/update', customer);
  return response.data; // e.g., { message, updated }
};

// 3. We remove deleteCustomer as it's not supported by the backend controller.