import { BrowserRouter, Routes, Route } from "react-router-dom";

// Layouts
import AppLayout from "./layouts/AppLayout";
import PublicLayout from "./layouts/PublicLayout";

// Route Protectors
import ProtectedRoute from "./components/common/ProtectedRoute";
import AdminRoute from "./components/common/AdminRoute";

// --- Pages ---

// Public Pages
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";

// Common Pages
import HomePage from "./pages/HomePage";

// User Pages
import UserAccountsPage from "./pages/UserAccountsPage";
import UserTransactionsPage from "./pages/UserTransactionsPage";
import ProfilePage from "./pages/ProfilePage";

// Admin Pages
import AdminCustomerPage from "./pages/AdminCustomerPage";
import AdminAccountsPage from "./pages/AdminAccountsPage";
import AdminTransactionsPage from "./pages/AdminTransactionsPage";

const App = () => {
    return (
        <BrowserRouter>
            <Routes>
                {/* --- Public Routes (Login, Signup) --- */}
                <Route element={<PublicLayout />}>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignupPage />} />
                </Route>

                {/* --- Protected Routes (Main App) --- */}
                <Route element={<ProtectedRoute layout={AppLayout} />}>
                    
                    {/* Common Routes */}
                    <Route path="/" element={<HomePage />} />
                    
                    {/* User Routes */}
                    <Route path="/accounts" element={<UserAccountsPage />} />
                    <Route path="/transactions" element={<UserTransactionsPage />} />
                    <Route path="/profile" element={<ProfilePage />} />

                    {/* Admin Routes */}
                    <Route element={<AdminRoute />}>
                        <Route path="/admin/customers" element={<AdminCustomerPage />} />
                        <Route path="/admin/accounts" element={<AdminAccountsPage />} />
                        <Route path="/admin/transactions" element={<AdminTransactionsPage />} />
                    </Route>

                </Route>
            </Routes>
        </BrowserRouter>
    );
};

export default App;