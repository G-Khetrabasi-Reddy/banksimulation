const CustomerDetails = ({ customer }) => {
  return (
    <div className="max-w-3xl mx-auto bg-white rounded-lg shadow-md overflow-hidden mt-6 border border-gray-200">
      <div className="bg-linear-to-r from-blue-500 to-blue-600 px-6 py-5">
        <h2 className="text-white text-xl font-semibold">Customer Details</h2>
      </div>

      <div className="p-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Name</p>
            <p className="text-gray-900 font-semibold text-lg">{customer.name}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Phone Number</p>
            <p className="text-gray-900 font-medium">{customer.phoneNumber}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Email</p>
            <p className="text-gray-900 font-medium break-all">{customer.email}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Aadhar Number</p>
            <p className="text-gray-900 font-medium font-mono">{customer.aadharNumber}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">PIN</p>
            <p className="text-gray-900 font-medium">{customer.customerPin}</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Date of Birth</p>
            <p className="text-gray-900 font-medium">{customer.dob}</p>
          </div>

          <div className="md:col-span-2 bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Address</p>
            <p className="text-gray-900 font-medium">{customer.address}</p>
          </div>

          <div className="md:col-span-2 bg-gray-50 rounded-lg p-4">
            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Status</p>
            <div className="flex items-center gap-2 mt-1">
              <span
                className={`inline-block w-2 h-2 rounded-full ${customer.status.toUpperCase() === "ACTIVE" ? 'bg-green-500' : 'bg-red-500'
                  }`}
              ></span>
              <p
                className={`font-semibold ${customer.status.toUpperCase === "ACTIVE" ? "text-green-600" : "text-red-600"
                  }`}
              >
                {customer.status}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CustomerDetails;