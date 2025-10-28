import React from 'react';

const AccountDetails = ({ account }) => {
  return (
    <div className="max-w-3xl mx-auto bg-white rounded-lg shadow-md overflow-hidden mt-6 border border-gray-200">
      <div className="bg-linear-to-r from-blue-500 to-blue-600 px-6 py-5">
        <h2 className="text-white text-xl font-semibold">Account Details</h2>
      </div>

      <div className="p-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Account Number</p>
            <p className="text-gray-900 font-semibold text-lg wrap-break-word">{account.accountNumber}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Account Name</p>
            <p className="text-gray-900 font-semibold text-lg">{account.accountName || '-'}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Customer ID</p>
            <p className="text-gray-900 font-semibold text-lg">{account.customerId}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Account Type</p>
            <p className="text-gray-900 font-medium">{account.accountType}</p>
          </div>

          <div className="bg-linear-to-br from-blue-50 to-blue-100 rounded-lg p-4 border border-blue-200">
            <p className="text-xs text-blue-700 font-medium uppercase tracking-wide mb-1">Balance</p>
            <p className="text-blue-700 font-bold text-2xl">₹{account.balance.toFixed(2)}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">IFSC Code</p>
            <p className="text-gray-900 font-medium">{account.ifscCode}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Status</p>
            <div className="flex items-center gap-2">
              <span
                className={`inline-block w-2 h-2 rounded-full ${account.status.toUpperCase() === 'ACTIVE'
                    ? 'bg-green-500'
                    : 'bg-red-500'
                  }`}
              ></span>
              <p
                className={`font-semibold ${account.status.toUpperCase() === 'ACTIVE'
                    ? 'text-green-600'
                    : 'text-red-600'
                  }`}
              >
                {account.status}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AccountDetails;