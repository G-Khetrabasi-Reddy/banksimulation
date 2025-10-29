import { Navigate } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

import Loader from './Loader'; 

const ProtectedRoute = ({ layout: LayoutComponent }) => {
    const { user, isLoading } = useAuth();

    if (isLoading) {
        return <Loader />;
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return <LayoutComponent />;
};

export default ProtectedRoute;