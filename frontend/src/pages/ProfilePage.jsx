import { useState } from "react";
import { updateCustomer } from "../api/customerApi";
import UpdateCustomerForm from "../components/customer/UpdateCustomerForm";
import useAuth from "../hooks/useAuth";
import { User, CheckCircle } from 'lucide-react';

const ProfilePage = () => {
  const { user, updateUser } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleUpdateProfile = async (updatedCustomer) => {
    setIsLoading(true);
    setError('');
    setSuccess('');
    try {
      const res = await updateCustomer(updatedCustomer);
      setSuccess(res.message || 'Profile updated successfully!');
      updateUser(updatedCustomer);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to update profile");
    } finally {
      setIsLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="min-h-screen bg-linear-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-lg shadow-md border border-gray-200 p-12 text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="text-gray-500 mt-4">Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
        <div className="flex items-center gap-3">
          <User size={32} className="text-blue-600" />
          <div>
            <h1 className="text-3xl font-bold text-gray-900">My Profile</h1>
            <p className="text-gray-500 mt-1">View and update your personal information</p>
          </div>
        </div>
      </div>

      {/* Success Message */}
      {success && (
        <div className="bg-green-50 border border-green-200 rounded-lg p-4">
          <div className="flex items-center gap-2">
            <CheckCircle size={18} className="text-green-600" />
            <p className="text-green-700 font-medium">{success}</p>
          </div>
        </div>
      )}

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-700 font-medium">{error}</p>
        </div>
      )}

      {/* Profile Form */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200">
        <UpdateCustomerForm
          existingCustomer={user}
          onUpdate={handleUpdateProfile}
          isLoading={isLoading}
        />
      </div>
    </div>
  );
};

export default ProfilePage;