import { Home, User, CreditCard, ArrowUp, UserCircle, LogOut, Mail, Menu, X } from "lucide-react";
import { NavLink } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import { useState, useEffect } from "react";
import { getAllCustomers } from "../../api/customerApi";

const Sidebar = ({ className = "" }) => {
    const { user, logout } = useAuth();
    const [isOpen, setIsOpen] = useState(false);

    const [adminEmail, setAdminEmail] = useState("banksimulation3@gmail.com");

    useEffect(() => {
      const fetchAdminEmail = async () => {
        try {
          const res = await getAllCustomers();
          const adminUser = res.customers?.find(c => c.role === 'ADMIN');
          if (adminUser && adminUser.email) {
            setAdminEmail(adminUser.email);
          }
        } catch (err) {
          console.error("Could not fetch admin email, using default.", err);
        }
      };
      if (user && user.role !== 'ADMIN') {
        fetchAdminEmail();
      }
    }, [user]);

    const adminLinks = [
        { name: "Home", icon: <Home size={20} />, path: "/" },
        { name: "Customers", icon: <User size={20} />, path: "/admin/customers" },
        { name: "Accounts", icon: <CreditCard size={20} />, path: "/admin/accounts" },
        { name: "Transactions", icon: <ArrowUp size={20} />, path: "/admin/transactions" },
    ];

    const userLinks = [
        { name: "Home", icon: <Home size={20} />, path: "/" },
        { name: "My Accounts", icon: <CreditCard size={20} />, path: "/accounts" },
        { name: "Transactions", icon: <ArrowUp size={20} />, path: "/transactions" },
        { name: "Profile", icon: <UserCircle size={20} />, path: "/profile" },
        {
            name: "Contact Admin",
            icon: <Mail size={20} />,
            path: `mailto:${adminEmail}?subject=Support Query`,
            isExternal: true
        },
    ];

    const menuItems = user?.role === 'ADMIN' ? adminLinks : userLinks;

    return (
    <>
      {/* Top Bar for Mobile */}
      <div className="md:hidden flex items-center justify-between bg-white border-b border-gray-200 px-4 py-3">
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="p-2 text-gray-600 hover:text-gray-800 focus:outline-none"
        >
          {isOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
        <h1 className="text-lg font-semibold text-gray-800">Menu</h1>
      </div>

      {/* Sidebar */}
      <aside
        className={`fixed md:static top-0 right-0 md:left-0 z-50 w-64 h-full bg-white border-gray-200 flex flex-col justify-between py-6 px-3 transform transition-transform duration-300
        ${isOpen ? "translate-x-0" : "translate-x-full"}
        md:translate-x-0 md:border-r ${className}`}
      >
        {/* Menu Items */}
        <ul className="space-y-1">
          {menuItems.map((item) => (
            <li key={item.name}>
              {item.isExternal ? (
                <a
                  href={item.path}
                  className="flex items-center gap-3 w-full text-left px-4 py-2.5 rounded-md transition text-gray-700 hover:bg-gray-50"
                >
                  {item.icon}
                  <span className="truncate">{item.name}</span>
                </a>
              ) : (
                <NavLink
                  to={item.path}
                  end
                  className={({ isActive }) =>
                    `flex items-center gap-3 w-full text-left px-4 py-2.5 rounded-md transition ${
                      isActive
                        ? "bg-blue-50 text-blue-600 font-medium"
                        : "text-gray-700 hover:bg-gray-50"
                    }`
                  }
                  onClick={() => setIsOpen(false)} // close sidebar on mobile click
                >
                  {item.icon}
                  <span className="truncate">{item.name}</span>
                </NavLink>
              )}
            </li>
          ))}
        </ul>

        {/* User Info & Logout */}
        <div className="border-t border-gray-200 pt-4 space-y-2">
          <div className="flex items-center gap-3 px-4 py-2">
            <UserCircle size={36} className="text-gray-400" />
            <div className="truncate flex-1">
              <p className="font-medium text-gray-900 truncate">
                {user?.name || "User"}
              </p>
              <p className="text-gray-500 text-xs truncate">{user?.role}</p>
            </div>
          </div>
          <button
            onClick={logout}
            className="flex items-center gap-3 w-full text-left text-red-600 hover:bg-red-50 px-4 py-2.5 rounded-md transition"
          >
            <LogOut size={20} />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      {/* Dimmed Background when sidebar open (mobile only) */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-10 backdrop-blur-sm transition-opacity duration-300 md:hidden"
          onClick={() => setIsOpen(false)}
        />
      )}
    </>
  );
};

export default Sidebar;