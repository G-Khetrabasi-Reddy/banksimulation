import { Outlet } from 'react-router-dom';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import Sidebar from '../components/common/Sidebar';

const AppLayout = () => {
    
    return (
        <div className="flex flex-col min-h-screen">
            <Navbar />
            <div className="flex flex-1 overflow-hidden">
                <div className="hidden md:block">
                    <Sidebar />
                </div>
                
                {/*Main content */}
                <main className="flex-1 overflow-auto bg-gray-50 p-4 max-w-6xl mx-auto w-full">
                    <Outlet />
                </main>
            </div>
            
            <Footer /> 
        </div>
    );
};

export default AppLayout;