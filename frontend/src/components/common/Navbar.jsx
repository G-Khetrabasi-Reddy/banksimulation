import Sidebar from "./Sidebar";
const Navbar = () => {
    return (
        <nav className="flex justify-between items-center px-6 py-4 border-b border-gray-200 bg-white">
            <h1 className="text-3xl font-semibold text-gray-900">🏦 Bank Simulation</h1> 
            <Sidebar className="md:hidden" />
        </nav>
    );
};
export default Navbar;