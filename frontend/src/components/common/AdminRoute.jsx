import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

const AdminRoute = () => {
    const { user } = useAuth(); // We know user exists thanks to ProtectedRoute

    if (user?.role !== 'ADMIN') {
        // User is logged in but not an admin
        return <Navigate to="/" replace />;
    }

    // User is an admin, render the requested admin page
    return <Outlet />;
};

export default AdminRoute;