import { useEffect, useState } from "react";
import { Users, Banknote, RefreshCcw, TrendingUp, ArrowUpRight, ArrowDownRight, Clock, CheckCircle, Building2, CreditCard, Wallet, Shield } from "lucide-react";
import useAuth from "../hooks/useAuth";
import { getAllCustomers } from "../api/customerApi";
import { getAllAccounts } from "../api/accountApi";
import { getAllTransactions } from "../api/transactionApi";

const HomePage = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  const [stats, setStats] = useState([
    {
      title: "Total Customers",
      icon: <Users size={28} />,
      bg: "bg-gradient-to-br from-blue-100 to-blue-50",
      color: "text-blue-600",
      value: "0",
    },
    {
      title: "Total Accounts",
      icon: <Banknote size={28} />,
      bg: "bg-gradient-to-br from-green-100 to-emerald-50",
      color: "text-green-600",
      value: "0",
    },
    {
      title: "Total Transactions",
      icon: <RefreshCcw size={28} />,
      bg: "bg-gradient-to-br from-purple-100 to-pink-50",
      color: "text-purple-600",
      value: "0",

    },
  ]);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [custRes, acctRes, transRes] = await Promise.all([
          getAllCustomers(),
          getAllAccounts(),
          getAllTransactions()
        ]);

        const custCount = custRes.customers?.length || 0;
        const acctCount = acctRes.accounts?.length || 0;
        const transCount = transRes.transactions?.length || 0;

        setStats([
          {
            title: "Total Customers",
            icon: <Users size={28} />,
            bg: "bg-gradient-to-br from-blue-100 to-blue-50",
            color: "text-blue-600",
            value: custCount.toLocaleString(),
          },
          {
            title: "Total Accounts",
            icon: <Banknote size={28} />,
            bg: "bg-gradient-to-br from-green-100 to-emerald-50",
            color: "text-green-600",
            value: acctCount.toLocaleString(),
          },
          {
            title: "Total Transactions",
            icon: <RefreshCcw size={28} />,
            bg: "bg-gradient-to-br from-purple-100 to-pink-50",
            color: "text-purple-600",
            value: transCount.toLocaleString(),
          },
        ]);

      } catch (err) {
        console.error("Failed to fetch homepage stats:", err);
      }
    };

    if (user) {
      fetchStats();
    }
  }, [user]);


  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-linear-to-r from-blue-600 to-blue-700 rounded-lg shadow-md border border-blue-700 p-8 text-white">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-3xl font-bold mb-2">
              Welcome back, {user?.name || 'User'}! 👋
            </h1>
            <p className="text-blue-100 text-lg">
              {isAdmin
                ? "Manage your banking platform from the admin dashboard"
                : "Your complete banking solution at your fingertips"}
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur-sm rounded-lg px-6 py-3 border border-white/20">
            <p className="text-blue-100 text-sm">Role</p>
            <p className="text-xl font-bold capitalize">{user?.role || 'User'}</p>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Platform Statistics</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {stats.map((item) => (
              <div
                key={item.title}
                className="group bg-linear-to-br from-gray-50 to-white rounded-lg border border-gray-200 shadow-sm hover:shadow-md transition p-5"
              >
                <div className="flex items-start justify-between mb-3">
                  <div className={`w-12 h-12 flex items-center justify-center rounded-full ${item.bg}`}>
                    <span className={item.color}>{item.icon}</span>
                  </div>
                  <div className={`flex items-center gap-1 text-sm font-medium ${item.isPositive ? 'text-green-600' : 'text-red-600'}`}>
                    {item.isPositive ? <ArrowUpRight size={16} /> : <ArrowDownRight size={16} />}
                    {item.change}
                  </div>
                </div>
                <p className="text-gray-500 text-sm font-medium mb-1">{item.title}</p>
                <p className="text-3xl font-bold text-gray-900">{item.value}</p>
              </div>
            ))}
         </div>
      </div>

      {/* Information Banner */}
      <div className="bg-linear-to-r from-indigo-50 to-blue-50 rounded-lg shadow-md border border-indigo-200 p-6">
        <div className="flex items-start gap-4">
          <div className="w-12 h-12 bg-indigo-600 rounded-full flex items-center justify-center text-white shrink-0">
            <Building2 size={24} />
          </div>
          <div>
            <h3 className="text-lg font-bold text-gray-900 mb-2">
              About Bank Simulation Platform
            </h3>
            <p className="text-gray-600 leading-relaxed">
              Our comprehensive banking simulation platform provides a complete suite of tools for managing
              customers, accounts, and transactions. Experience modern banking features with real-time updates,
              secure transactions, and intuitive management tools designed for both administrators and users.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;