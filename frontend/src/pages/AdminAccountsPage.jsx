import { useEffect, useState } from "react";
import {
  getAllAccounts,
  getAccountDetails,
  getAccountBalance,
} from "../api/accountApi";
import AccountList from "../components/account/AccountList";
import AccountDetails from "../components/account/AccountDetails";
import { Search, List, CreditCard, DollarSign } from 'lucide-react';

const AdminAccountsPage = () => {
  const [accounts, setAccounts] = useState([]);
  const [activeOperation, setActiveOperation] = useState('all');
  const [searchNumber, setSearchNumber] = useState("");
  const [foundAccount, setFoundAccount] = useState(null);
  const [accountBalance, setAccountBalance] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    handleFetchAll();
  }, []);

  const handleFetchAll = async () => {
    setIsLoading(true);
    setError('');
    try {
      const res = await getAllAccounts();
      setAccounts(res.accounts || []);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to fetch accounts");
    } finally {
      setIsLoading(false);
    }
  };

  const handleGetDetails = async () => {
    if (!searchNumber) {
      setError('Please enter an Account Number.');
      return;
    }
    setAccountBalance(null);
    setIsLoading(true);
    setError('');
    try {
      const res = await getAccountDetails(searchNumber);
      setFoundAccount(res || null);
    } catch (err) {
      setFoundAccount(null);
      console.error(err);
      setError(err.response?.data?.message || "Account not found");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCheckBalance = async () => {
    if (!searchNumber) {
      setError('Please enter an Account Number.');
      return;
    }
    setFoundAccount(null);
    setIsLoading(true);
    setError('');
    try {
      const res = await getAccountBalance(searchNumber);
      setAccountBalance(res.balance);
    } catch (err) {
      setAccountBalance(null);
      console.error(err);
      setError(err.response?.data?.message || "Account not found");
    } finally {
      setIsLoading(false);
    }
  };

  const switchOperation = (op) => {
    setActiveOperation(op);
    setSearchNumber("");
    setFoundAccount(null);
    setAccountBalance(null);
    setError('');
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
        <div className="flex items-center gap-3">
          <CreditCard size={32} className="text-blue-600" />
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Account Management</h1>
            <p className="text-gray-500 mt-1">View, search, and manage bank accounts</p>
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
            All Accounts
          </button>
          <button
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'find'
                ? 'bg-blue-600 text-white shadow-md'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            onClick={() => switchOperation('find')}
          >
            <Search size={18} />
            Find Account
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
        {/* View All Accounts */}
        {activeOperation === 'all' && (
          <>
            {isLoading ? (
              <div className="bg-white rounded-lg shadow-md border border-gray-200 p-12 text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                <p className="text-gray-500 mt-4">Loading accounts...</p>
              </div>
            ) : (
              <AccountList accounts={accounts} />
            )}
          </>
        )}

        {/* Find Account */}
        {activeOperation === 'find' && (
          <div className="bg-white rounded-lg shadow-md border border-gray-200 p-8">
            <div className="max-w-3xl mx-auto">
              <div className="mb-6">
                <h3 className="text-xl font-semibold text-gray-900">Find Account Details or Balance</h3>
                <p className="text-gray-500 text-sm mt-1">Enter account number to view details or check balance</p>
              </div>

              <div className="flex flex-col sm:flex-row gap-3 mb-6">
                <input
                  type="text"
                  placeholder="Enter Account Number"
                  value={searchNumber}
                  onChange={(e) => setSearchNumber(e.target.value)}
                  className="flex-1 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                />
                <div className="flex gap-3">
                  <button
                    onClick={handleGetDetails}
                    className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center gap-2"
                    disabled={isLoading}
                  >
                    <Search size={18} />
                    {isLoading ? 'Loading...' : 'Get Details'}
                  </button>
                  <button
                    onClick={handleCheckBalance}
                    className="bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center gap-2"
                    disabled={isLoading}
                  >
                    <DollarSign size={18} />
                    {isLoading ? 'Loading...' : 'Check Balance'}
                  </button>
                </div>
              </div>

              {/* Results Area */}
              {foundAccount && <AccountDetails account={foundAccount} />}

              {accountBalance !== null && (
                <div className="bg-linear-to-br from-green-50 to-green-100 rounded-lg shadow-md border border-green-200 p-8 text-center mt-6">
                  <div className="inline-block bg-green-500 rounded-full p-3 mb-4">
                    <DollarSign size={32} className="text-white" />
                  </div>
                  <p className="text-gray-600 text-sm font-medium uppercase tracking-wide mb-2">Available Balance</p>
                  <p className="text-4xl font-bold text-green-700">₹{accountBalance.toFixed(2)}</p>
                  <p className="text-gray-500 text-sm mt-2">Account: {searchNumber}</p>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminAccountsPage;