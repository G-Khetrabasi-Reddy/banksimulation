import { useEffect, useState } from "react";
import CustomerDetails from "../components/customer/CustomerDetails";
import CustomerList from "../components/customer/CustomerList";
import UpdateCustomerForm from "../components/customer/UpdateCustomerForm";
import {
  getCustomerById,
  getAllCustomers,
  updateCustomer
} from "../api/customerApi";
import { Search, List, Edit, Users } from 'lucide-react';

const AdminCustomerPage = () => {
  const [customers, setCustomers] = useState([]);
  const [activeOperation, setActiveOperation] = useState('all');
  const [searchId, setSearchId] = useState("");
  const [foundCustomer, setFoundCustomer] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    handleFetchAll();
  }, []);

  const handleFetchAll = async () => {
    setIsLoading(true);
    setError('');
    try {
      const res = await getAllCustomers();
      setCustomers(res.customers || []);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to fetch customers");
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchId) {
      setError('Please enter a Customer ID.');
      return;
    }
    setIsLoading(true);
    setError('');
    setFoundCustomer(null);
    try {
      const res = await getCustomerById(searchId);
      setFoundCustomer(res.customer || null);
      if (!res.customer) {
        setError("Customer not found!");
      }
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Error fetching customer");
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateCustomer = async (updatedCustomer) => {
    setIsLoading(true);
    setError('');
    try {
      const res = await updateCustomer(updatedCustomer);
      alert(res.message);
      handleFetchAll();
      setFoundCustomer(null);
      setSearchId("");
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to update customer");
    } finally {
      setIsLoading(false);
    }
  };

  const switchOperation = (op) => {
    setActiveOperation(op);
    setSearchId("");
    setFoundCustomer(null);
    setError('');
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
        <div className="flex items-center gap-3">
          <Users size={32} className="text-blue-600" />
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Customer Management</h1>
            <p className="text-gray-500 mt-1">View, search, and update customer records</p>
          </div>
        </div>
      </div>

      {/* Operation Tabs */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-4">
        <div className="flex flex-wrap gap-3">
          <button
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'all'
                ? 'bg-blue-600 text-white shadow-md'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            onClick={() => {
              switchOperation('all');
              handleFetchAll();
            }}
          >
            <List size={18} />
            All Customers
          </button>
          <button
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'find'
                ? 'bg-blue-600 text-white shadow-md'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            onClick={() => switchOperation('find')}
          >
            <Search size={18} />
            Find by ID
          </button>
          <button
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'update'
                ? 'bg-blue-600 text-white shadow-md'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            onClick={() => switchOperation('update')}
          >
            <Edit size={18} />
            Update Customer
          </button>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-700 font-medium">{error}</p>
        </div>
      )}

      {/* Content Area */}
      <div>
        {/* All Customers */}
        {activeOperation === 'all' && (
          <>
            {isLoading ? (
              <div className="bg-white rounded-lg shadow-md border border-gray-200 p-12 text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                <p className="text-gray-500 mt-4">Loading customers...</p>
              </div>
            ) : (
              <CustomerList customers={customers} />
            )}
          </>
        )}

        {/* Find Customer */}
        {activeOperation === 'find' && (
          <div className="bg-white rounded-lg shadow-md border border-gray-200 p-8">
            <div className="max-w-2xl mx-auto">
              <div className="mb-6">
                <h3 className="text-xl font-semibold text-gray-900">Find Customer by ID</h3>
                <p className="text-gray-500 text-sm mt-1">Enter a customer ID to view details</p>
              </div>

              <div className="flex gap-3 mb-6">
                <input
                  type="text"
                  placeholder="Enter Customer ID"
                  value={searchId}
                  onChange={(e) => setSearchId(e.target.value)}
                  className="flex-1 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                />
                <button
                  onClick={handleSearch}
                  className="bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center gap-2"
                  disabled={isLoading}
                >
                  <Search size={18} />
                  {isLoading ? 'Searching...' : 'Search'}
                </button>
              </div>

              {foundCustomer && <CustomerDetails customer={foundCustomer} />}
            </div>
          </div>
        )}

        {/* Update Customer */}
        {activeOperation === 'update' && (
          <div className="bg-white rounded-lg shadow-md border border-gray-200 p-8">
            <div className="max-w-3xl mx-auto">
              <div className="mb-6">
                <h3 className="text-xl font-semibold text-gray-900">Update Customer</h3>
                <p className="text-gray-500 text-sm mt-1">Search for a customer to update their information</p>
              </div>

              <div className="flex gap-3 mb-6">
                <input
                  type="text"
                  placeholder="Enter Customer ID to edit"
                  value={searchId}
                  onChange={(e) => setSearchId(e.target.value)}
                  className="flex-1 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                />
                <button
                  onClick={handleSearch}
                  className="bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center gap-2"
                  disabled={isLoading}
                >
                  <Edit size={18} />
                  {isLoading ? 'Loading...' : 'Load Customer'}
                </button>
              </div>

              {foundCustomer && (
                <UpdateCustomerForm
                  existingCustomer={foundCustomer}
                  onUpdate={handleUpdateCustomer}
                />
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminCustomerPage;