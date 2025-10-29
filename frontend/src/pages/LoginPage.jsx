import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import { LogIn, Mail, Lock, UserPlus } from 'lucide-react';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const auth = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);
        try {
            await auth.login(email, password);
            navigate('/');
        } catch (err) {
            console.error(err);
            setError('Login failed. Please check your email and password.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-linear-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
            <div className="w-[28rem] max-w-full space-y-6">
                {/* Header */}
                <div className="w-full bg-white rounded-lg shadow-md border border-gray-200 p-6">
                    <div className="flex items-center gap-3">
                        <LogIn size={32} className="text-blue-600" />
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">Welcome Back</h1>
                            <p className="text-gray-500 mt-1">Login to access your account</p>
                        </div>
                    </div>
                </div>

                {/* Login Form */}
                <div className="w-full bg-white rounded-lg shadow-md border border-gray-200 p-8">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">
                                Email Address
                            </label>
                            <div className="relative">
                                <Mail size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                                    placeholder="Enter your email"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-gray-700 font-medium mb-2">
                                Password
                            </label>
                            <div className="relative">
                                <Lock size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                                    placeholder="Enter your password"
                                    required
                                />
                            </div>
                        </div>

                        {/* Error Message */}
                        <div className="min-h-[3.5rem]">
                            {error && (
                                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                                    <p className="text-red-700 font-medium text-sm">{error}</p>
                                </div>
                            )}
                        </div>

                        <button
                            type="submit"
                            className="w-full bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center justify-center gap-2"
                            disabled={isLoading}
                        >
                            <LogIn size={18} />
                            {isLoading ? 'Logging in...' : 'Login'}
                        </button>
                    </form>
                </div>

                {/* Signup Link */}
                <div className="w-full bg-white rounded-lg shadow-md border border-gray-200 p-4">
                    <p className="text-center text-gray-600">
                        Don't have an account?{' '}
                        <Link
                            to="/signup"
                            className="font-medium text-blue-600 hover:text-blue-700 transition inline-flex items-center gap-1"
                        >
                            <UserPlus size={16} />
                            Sign Up
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;