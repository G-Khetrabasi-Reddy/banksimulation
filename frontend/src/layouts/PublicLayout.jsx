import { Outlet } from 'react-router-dom';

const PublicLayout = () => {
    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
            <Outlet />
        </div>
    );
};

export default PublicLayout;