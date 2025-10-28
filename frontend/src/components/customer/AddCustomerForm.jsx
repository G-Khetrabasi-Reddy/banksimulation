import { useState } from "react";
import { User, Phone, Mail, MapPin, Hash, Calendar, Lock, CreditCard, CheckCircle, RotateCcw, UserPlus } from "lucide-react";

const AddCustomerForm = ({ onAdd, isSignup = false }) => {
  const [customer, setCustomer] = useState({
    name: "",
    phone: "",
    email: "",
    address: "",
    pin: "",
    aadharNumber: "",
    dob: "",
    status: "Active",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCustomer({ ...customer, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setShowSuccess(false);

    const payload = {
      name: customer.name,
      phoneNumber: customer.phone,
      email: customer.email,
      address: customer.address,
      customerPin: customer.pin,
      aadharNumber: customer.aadharNumber,
      dob: customer.dob,
      status: customer.status.toUpperCase(),
      ...(isSignup && { password: customer.password }),
    };

    try {
      if (onAdd) {
        await onAdd(payload);
      }

      setShowSuccess(true);
      setTimeout(() => setShowSuccess(false), 3000);
      handleReset();
    } catch (error) {
      console.error("Error adding customer:", error.response?.data || error.message);
      alert(error.response?.data?.message || "Failed to add customer");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setCustomer({
      name: "",
      phone: "",
      email: "",
      address: "",
      pin: "",
      aadharNumber: "",
      dob: "",
      status: "ACTIVE",
      password: "",
    });
  };

  return (
    <div className="max-w-4xl mx-auto">
      {/* Success Message */}
      {showSuccess && (
        <div className="mb-6 bg-green-50 border border-green-200 rounded-lg p-4 animate-fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle size={20} className="text-green-600" />
            <p className="text-green-700 font-medium">
              {isSignup ? "Account created successfully!" : "Customer added successfully!"}
            </p>
          </div>
        </div>
      )}

      <div className="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden">
        {/* Form Header */}
        <div className="bg-linear-to-r from-blue-600 to-blue-700 p-6">
          <div className="flex items-center gap-3 text-white">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
              <UserPlus size={24} />
            </div>
            <div>
              <h2 className="text-2xl font-bold">
                {isSignup ? "Create Your Account" : "Add New Customer"}
              </h2>
              <p className="text-blue-100 text-sm mt-1">
                {isSignup ? "Fill in your details to get started" : "Enter customer information below"}
              </p>
            </div>
          </div>
        </div>

        {/* Form Content */}
        <form onSubmit={handleSubmit} className="p-8">
          {/* Personal Information Section */}
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4 pb-2 border-b border-gray-200">
              <User size={20} className="text-blue-600" />
              <h3 className="text-lg font-semibold text-gray-900">Personal Information</h3>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Name */}
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Full Name <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <User size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    name="name"
                    value={customer.name}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    placeholder="Enter full name"
                    required
                  />
                </div>
              </div>

              {/* Phone */}
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Phone Number <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <Phone size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="tel"
                    name="phone"
                    value={customer.phone}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    placeholder="Enter phone number"
                    required
                  />
                </div>
              </div>

              {/* Email */}
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email Address <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <Mail size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="email"
                    name="email"
                    value={customer.email}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    placeholder="Enter email address"
                    required
                  />
                </div>
              </div>

              {/* Date of Birth */}
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Date of Birth <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <Calendar size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="date"
                    name="dob"
                    value={customer.dob}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    required
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Verification Details Section */}
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4 pb-2 border-b border-gray-200">
              <CreditCard size={20} className="text-blue-600" />
              <h3 className="text-lg font-semibold text-gray-900">Verification Details</h3>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Aadhar Number */}
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Aadhar Number <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <Hash size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    name="aadharNumber"
                    value={customer.aadharNumber}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    placeholder="Enter 12-digit Aadhar number"
                    maxLength="12"
                    required
                  />
                </div>
              </div>

              {/* PIN */}
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  PIN Code <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <MapPin size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    name="pin"
                    value={customer.pin}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    placeholder="Enter 6-digit PIN code"
                    maxLength="6"
                    required
                  />
                </div>
              </div>

              {/* Password (Conditional) */}
              {isSignup && (
                <div className="relative">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Password <span className="text-red-500">*</span>
                  </label>
                  <div className="relative">
                    <Lock size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                    <input
                      type="password"
                      name="password"
                      value={customer.password}
                      onChange={handleChange}
                      className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                      placeholder="Create a strong password"
                      required
                    />
                  </div>
                  <p className="text-xs text-gray-500 mt-1">Must be at least 8 characters</p>
                </div>
              )}

              {/* Status (Hidden for signup) */}
              {!isSignup && (
                <div className="relative">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Account Status <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="status"
                    value={customer.status}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-4 py-3 bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition appearance-none"
                    style={{
                      backgroundImage: `url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e")`,
                      backgroundPosition: 'right 0.5rem center',
                      backgroundRepeat: 'no-repeat',
                      backgroundSize: '1.5em 1.5em',
                      paddingRight: '2.5rem',
                    }}
                  >
                    <option value="Active">Active</option>
                    <option value="Inactive">Inactive</option>
                  </select>
                </div>
              )}
            </div>
          </div>

          {/* Address Section */}
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4 pb-2 border-b border-gray-200">
              <MapPin size={20} className="text-blue-600" />
              <h3 className="text-lg font-semibold text-gray-900">Address Information</h3>
            </div>

            <div className="relative">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Complete Address <span className="text-red-500">*</span>
              </label>
              <textarea
                name="address"
                value={customer.address}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition resize-none"
                placeholder="Enter complete address with street, city, and state"
                rows={4}
                required
              />
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex flex-col sm:flex-row gap-4 pt-4 border-t border-gray-200">
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                  {isSignup ? "Creating Account..." : "Adding Customer..."}
                </>
              ) : (
                <>
                  <CheckCircle size={18} />
                  {isSignup ? "Create Account" : "Add Customer"}
                </>
              )}
            </button>
            <button
              type="button"
              onClick={handleReset}
              disabled={loading}
              className="flex-1 sm:flex-none bg-gray-100 text-gray-700 px-8 py-3 rounded-lg hover:bg-gray-200 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <RotateCcw size={18} />
              Reset Form
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddCustomerForm;