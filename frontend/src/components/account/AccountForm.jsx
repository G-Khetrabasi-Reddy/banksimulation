import { useState } from "react";

const AccountForm = ({ onAccountOpened }) => {
  const initialState = {
    customerId: "",
    accountType: "SAVINGS",
    balance: 0.0,
    accountNumber: "",
    ifscCode: "",
    accountName: "",
  };

  const [account, setAccount] = useState(initialState);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setAccount({ ...account, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await onAccountOpened(account);
      alert("Account opened successfully!");
      setAccount(initialState);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to open account.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow-md border border-gray-200">
      <div className="mb-6">
        <h2 className="text-2xl font-semibold text-gray-900">Open New Account</h2>
        <p className="text-gray-500 text-sm mt-1">Fill in the details to create a new bank account</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Customer ID */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Customer ID
            </label>
            <input
              type="number"
              name="customerId"
              value={account.customerId}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter customer ID"
              required
            />
          </div>

          {/* Account Type */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Account Type
            </label>
            <select
              name="accountType"
              value={account.accountType}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
            >
              <option value="SAVINGS">Savings Account</option>
              <option value="CURRENT">Current Account</option>
            </select>
          </div>

          {/* Account Number */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Account Number
            </label>
            <input
              type="text"
              name="accountNumber"
              value={account.accountNumber}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter account number"
              required
            />
          </div>

          {/* IFSC Code */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              IFSC Code
            </label>
            <input
              type="text"
              name="ifscCode"
              value={account.ifscCode}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter IFSC code"
              required
            />
          </div>

          {/* Account Name */}
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Account Name (e.g., "Priya SBI Savings")
            </label>
            <input
              type="text"
              name="accountName"
              value={account.accountName}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter a nickname for the account"
            />
          </div>
        </div>

        {/* Initial Balance */}
        <div className="bg-blue-50 rounded-lg p-4 border border-blue-200">
          <label className="block text-sm font-medium text-blue-900 mb-2">
            Initial Balance
          </label>
          <div className="relative">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-blue-700 font-semibold">₹</span>
            <input
              type="number"
              name="balance"
              value={account.balance}
              onChange={handleChange}
              className="w-full border border-blue-300 rounded-lg pl-8 pr-4 py-2.5 bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="0.00"
              required
              min="0"
              step="0.01"
            />
          </div>
        </div>

        {/* Error Message */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
            <p className="text-red-700 text-sm font-medium">{error}</p>
          </div>
        )}

        {/* Submit Button */}
        <div className="flex gap-4 pt-2">
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
          >
            {loading ? "Opening Account..." : "Open Account"}
          </button>
          <button
            type="button"
            onClick={() => setAccount(initialState)}
            className="bg-gray-100 text-gray-700 px-8 py-3 rounded-lg hover:bg-gray-200 transition font-medium"
          >
            Reset
          </button>
        </div>
      </form>
    </div>
  );
};

export default AccountForm;