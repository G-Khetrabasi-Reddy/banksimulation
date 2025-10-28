import React, { useState, useEffect } from 'react';
import { getAllTransactions, getTransactionById } from '../api/transactionApi';
import TransactionList from '../components/transaction/TransactionList';
import TransactionDetails from '../components/transaction/TransactionDetails';
import ExportCSVButton from '../components/transaction/ExportCSVButton';
import { Search, List, Download } from 'lucide-react';

const AdminTransactionsPage = () => {
    const [transactions, setTransactions] = useState([]);
    const [activeOperation, setActiveOperation] = useState('all');
    const [searchId, setSearchId] = useState('');
    const [foundTransaction, setFoundTransaction] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (activeOperation === 'all') {
            handleFetchAll();
        }
    }, [activeOperation]);

    const handleFetchAll = async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await getAllTransactions();
            setTransactions(res.transactions || []);
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to fetch transactions');
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    };

    const handleSearchById = async () => {
        if (!searchId) {
            setError('Please enter a Transaction ID.');
            return;
        }
        setIsLoading(true);
        setError('');
        setFoundTransaction(null);
        try {
            const res = await getTransactionById(parseInt(searchId, 10));
            setFoundTransaction(res.transaction || null);
            if (!res.transaction) {
                setError('Transaction not found.');
            }
        } catch (err) {
            setError(err.response?.data?.error || 'Error fetching transaction');
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    };

    const switchOperation = (op) => {
        setActiveOperation(op);
        setSearchId("");
        setFoundTransaction(null);
        setError('');
    };

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
                <h1 className="text-3xl font-bold text-gray-900">Transaction Management</h1>
                <p className="text-gray-500 mt-2">View, search, and export transaction records</p>
            </div>

            {/* Operation Tabs */}
            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-4">
                <div className="flex flex-wrap gap-3">
                    <button
                        className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'all'
                                ? 'bg-blue-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            }`}
                        onClick={() => { switchOperation('all'); handleFetchAll(); }}
                    >
                        <List size={18} />
                        View All Transactions
                    </button>
                    <button
                        className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'findById'
                                ? 'bg-blue-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            }`}
                        onClick={() => switchOperation('findById')}
                    >
                        <Search size={18} />
                        Find by ID
                    </button>
                    <button
                        className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'export'
                                ? 'bg-blue-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            }`}
                        onClick={() => switchOperation('export')}
                    >
                        <Download size={18} />
                        Export CSV
                    </button>
                </div>
            </div>

            {/* Error Message */}
            {error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                    <p className="text-red-700 font-medium">{error}</p>
                </div>
            )}

            {/* Content Area */}
            <div>
                {/* View All Transactions */}
                {activeOperation === 'all' && (
                    <>
                        {isLoading ? (
                            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-12 text-center">
                                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                                <p className="text-gray-500 mt-4">Loading transactions...</p>
                            </div>
                        ) : (
                            <TransactionList transactions={transactions} />
                        )}
                    </>
                )}

                {/* Find by Transaction ID */}
                {activeOperation === 'findById' && (
                    <div className="bg-white rounded-lg shadow-md border border-gray-200 p-8">
                        <div className="max-w-2xl mx-auto">
                            <div className="mb-6">
                                <h3 className="text-xl font-semibold text-gray-900">Find Transaction by ID</h3>
                                <p className="text-gray-500 text-sm mt-1">Enter a transaction ID to view details</p>
                            </div>

                            <div className="flex gap-3 mb-6">
                                <input
                                    type="number"
                                    placeholder="Enter Transaction ID"
                                    value={searchId}
                                    onChange={(e) => setSearchId(e.target.value)}
                                    className="flex-1 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                                    min="1"
                                />
                                <button
                                    onClick={handleSearchById}
                                    className="bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center gap-2"
                                    disabled={isLoading}
                                >
                                    <Search size={18} />
                                    {isLoading ? 'Searching...' : 'Search'}
                                </button>
                            </div>

                            {foundTransaction && <TransactionDetails transaction={foundTransaction} />}
                        </div>
                    </div>
                )}

                {/* Export CSV */}
                {activeOperation === 'export' && (
                    <ExportCSVButton />
                )}
            </div>
        </div>
    );
};

export default AdminTransactionsPage;