import { useState, useEffect } from "react";

const UpdateCustomerForm = ({ existingCustomer, onUpdate }) => {
  const [customer, setCustomer] = useState({
    customerId: "",
    name: "",
    phoneNumber: "",
    email: "",
    address: "",
    customerPin: "",
    aadharNumber: "",
    dob: "",
    status: "Active",
  });

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (existingCustomer) {
      setCustomer({ ...existingCustomer });
    }
  }, [existingCustomer]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCustomer({ ...customer, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      console.log("Updated Customer:", customer);
      if (onUpdate) await onUpdate(customer);
      alert("Customer updated successfully!");
    } catch (error) {
      console.error("Update failed:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setCustomer({ ...existingCustomer });
  };

  return (
    <div className="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow-md border border-gray-200">
      <div className="mb-6">
        <h2 className="text-2xl font-semibold text-gray-900">Update Customer</h2>
        <p className="text-gray-500 text-sm mt-1">Modify customer information below</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Customer ID */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Customer ID
            </label>
            <input
              type="text"
              name="customerId"
              value={customer.customerId}
              readOnly
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 bg-gray-100 cursor-not-allowed text-gray-600"
            />
          </div>

          {/* Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Name
            </label>
            <input
              type="text"
              name="name"
              value={customer.name}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter full name"
              required
            />
          </div>

          {/* Phone */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Phone Number
            </label>
            <input
              type="tel"
              name="phoneNumber"
              value={customer.phoneNumber}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter phone number"
              required
            />
          </div>

          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Email
            </label>
            <input
              type="email"
              name="email"
              value={customer.email}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter email address"
              required
            />
          </div>

          {/* Aadhar Number */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Aadhar Number
            </label>
            <input
              type="text"
              name="aadharNumber"
              value={customer.aadharNumber}
              readOnly
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 bg-gray-100 cursor-not-allowed text-gray-600"
            />
          </div>

          {/* PIN */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              PIN
            </label>
            <input
              type="text"
              name="customerPin"
              value={customer.customerPin}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Enter PIN"
              required
            />
          </div>

          {/* Date of Birth */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Date of Birth
            </label>
            <input
              type="date"
              name="dob"
              value={customer.dob}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              required
            />
          </div>

          {/* Status */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Status
            </label>
            <select
              name="status"
              value={customer.status}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
            >
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
            </select>
          </div>
        </div>

        {/* Address */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Address
          </label>
          <textarea
            name="address"
            value={customer.address}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
            placeholder="Enter complete address"
            rows={3}
            required
          />
        </div>

        {/* Buttons */}
        <div className="flex gap-4 pt-2">
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
          >
            {loading ? "Updating..." : "Update"}
          </button>
          <button
            type="button"
            onClick={handleReset}
            className="bg-gray-100 text-gray-700 px-8 py-3 rounded-lg hover:bg-gray-200 transition font-medium"
          >
            Reset
          </button>
        </div>
      </form>
    </div>
  );
};

export default UpdateCustomerForm;