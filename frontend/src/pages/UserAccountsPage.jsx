import { useEffect, useState } from "react";
import {
  openAccount,
  getAllAccounts,
  closeAccount,
} from "../api/accountApi";
import AccountForm from "../components/account/AccountForm";
import AccountList from "../components/account/AccountList";
import useAuth from "../hooks/useAuth";
import { CreditCard, Plus, List, XCircle } from 'lucide-react';

const UserAccountsPage = () => {
  const [myAccounts, setMyAccounts] = useState([]);
  const [activeOperation, setActiveOperation] = useState('all');
  const [searchNumber, setSearchNumber] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      handleFetchMyAccounts();
    }
  }, [user]);

  const handleFetchMyAccounts = async () => {
    if (!user) return;
    setIsLoading(true);
    setError('');
    try {
      const res = await getAllAccounts();
      const userAccounts = (res.accounts || []).filter(
        (acc) => acc.customerId === user.customerId
      );
      setMyAccounts(userAccounts);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to fetch accounts");
    } finally {
      setIsLoading(false);
    }
  };

  const handleOpenAccount = async (accountData) => {
    try {
      const dataWithUserId = { ...accountData, customerId: user.customerId };
      await openAccount(dataWithUserId);
      await handleFetchMyAccounts();
    } catch (err) {
      console.error("Open account error:", err.response?.data || err.message);
      throw err;
    }
  };

  const handleCloseAccount = async () => {
    if (!searchNumber) {
      setError('Please enter one of your account numbers.');
      return;
    }
    if (!myAccounts.find(acc => acc.accountNumber === searchNumber)) {
      setError('That account number does not belong to you.');
      return;
    }
    if (!window.confirm(`Are you sure you want to CLOSE account: ${searchNumber}?`)) {
      return;
    }

    setIsLoading(true);
    setError('');
    try {
      const res = await closeAccount(searchNumber);
      alert(res.message);
      await handleFetchMyAccounts();
      setSearchNumber("");
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to close account");
    } finally {
      setIsLoading(false);
    }
  };

  const switchOperation = (op) => {
    setActiveOperation(op);
    setSearchNumber("");
    setError('');
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
        <div className="flex items-center gap-3">
          <CreditCard size={32} className="text-blue-600" />
          <div>
            <h1 className="text-3xl font-bold text-gray-900">My Accounts</h1>
            <p className="text-gray-500 mt-1">Manage your personal bank accounts</p>
          </div>
        </div>
      </div>

      {/* Operation Tabs */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-4">
        <div className="flex flex-wrap gap-3">
          <button
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'open'
                ? 'bg-blue-600 text-white shadow-md'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            onClick={() => switchOperation('open')}
          >
            <Plus size={18} />
            Open New Account
          </button>
          <button
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'all'
                ? 'bg-blue-600 text-white shadow-md'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            onClick={() => {
              switchOperation('all');
              handleFetchMyAccounts();
            }}
          >
            <List size={18} />
            View All My Accounts
          </button>
          <button
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'close'
                ? 'bg-blue-600 text-white shadow-md'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            onClick={() => switchOperation('close')}
          >
            <XCircle size={18} />
            Close an Account
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
        {/* Open Account */}
        {activeOperation === 'open' && (
          <AccountForm onAccountOpened={handleOpenAccount} />
        )}

        {/* View All Accounts */}
        {activeOperation === 'all' && (
          <>
            {isLoading ? (
              <div className="bg-white rounded-lg shadow-md border border-gray-200 p-12 text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                <p className="text-gray-500 mt-4">Loading accounts...</p>
              </div>
            ) : (
              <AccountList accounts={myAccounts} />
            )}
          </>
        )}

        {/* Close Account */}
        {activeOperation === 'close' && (
          <div className="bg-white rounded-lg shadow-md border border-gray-200 p-8">
            <div className="max-w-3xl mx-auto">
              <div className="mb-6">
                <h3 className="text-xl font-semibold text-gray-900">Close Account</h3>
                <p className="text-gray-500 text-sm mt-1">Enter the account number you wish to close permanently</p>
              </div>

              <div className="flex flex-col sm:flex-row gap-3">
                <input
                  type="text"
                  placeholder="Enter Account Number"
                  value={searchNumber}
                  onChange={(e) => setSearchNumber(e.target.value)}
                  className="flex-1 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                />
                <button
                  onClick={handleCloseAccount}
                  className="bg-red-600 text-white px-6 py-3 rounded-lg hover:bg-red-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center gap-2 justify-center"
                  disabled={isLoading}
                >
                  <XCircle size={18} />
                  {isLoading ? 'Processing...' : 'Close Account'}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserAccountsPage;