import { Navigate } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

// We assume you have a Loader component as specified in the file list
// If not, you can replace <Loader /> with <p>Loading...</p>
import Loader from './Loader'; 

const ProtectedRoute = ({ layout: LayoutComponent }) => {
    const { user, isLoading } = useAuth();

    if (isLoading) {
        // Wait while AuthContext is checking for a token
        return <Loader />;
    }

    if (!user) {
        // User is not logged in, redirect to login
        return <Navigate to="/login" replace />;
    }

    // User is logged in, render the main AppLayout
    return <LayoutComponent />;
};

export default ProtectedRoute;