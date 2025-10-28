import { useNavigate, Link } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import AddCustomerForm from '../components/customer/AddCustomerForm';
import { UserPlus, LogIn } from 'lucide-react';

const SignupPage = () => {
    const auth = useAuth();
    const navigate = useNavigate();

    const handleSignup = async (customerData) => {
        try {
            await auth.signup(customerData);
            navigate('/');
        } catch (err) {
            console.error(err);
            alert("Signup Failed: " + (err.response?.data?.message || err.message));
        }
    };

    return (
        <div className="min-h-screen bg-linear-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
            <div className="w-full max-w-4xl space-y-6">
                {/* Header */}
                <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
                    <div className="flex items-center gap-3">
                        <UserPlus size={32} className="text-blue-600" />
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">Create Your Account</h1>
                            <p className="text-gray-500 mt-1">Sign up to get started with your banking journey</p>
                        </div>
                    </div>
                </div>

                {/* Form Container */}
                <div className="bg-white rounded-lg shadow-md border border-gray-200">
                    <AddCustomerForm onAdd={handleSignup} isSignup={true} />
                </div>

                {/* Login Link */}
                <div className="bg-white rounded-lg shadow-md border border-gray-200 p-4">
                    <p className="text-center text-gray-600">
                        Already have an account?{' '}
                        <Link
                            to="/login"
                            className="font-medium text-blue-600 hover:text-blue-700 transition inline-flex items-center gap-1"
                        >
                            <LogIn size={16} />
                            Log In
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default SignupPage;