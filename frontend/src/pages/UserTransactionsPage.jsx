import React, { useState } from 'react';
import TransferForm from '../components/transaction/TransferForm';
import ExportCSVButton from '../components/transaction/ExportCSVButton';
import { ArrowLeftRight, Download, Receipt } from 'lucide-react';

const UserTransactionsPage = () => {
    const [activeOperation, setActiveOperation] = useState('transfer');
    const [error, setError] = useState('');

    const handleTransferSuccess = () => {
        alert("Transfer successful!");
    };

    const switchOperation = (op) => {
        setActiveOperation(op);
        setError('');
    };

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6">
                <div className="flex items-center gap-3">
                    <Receipt size={32} className="text-blue-600" />
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900">My Transactions</h1>
                        <p className="text-gray-500 mt-1">Transfer money and export transaction history</p>
                    </div>
                </div>
            </div>

            {/* Operation Tabs */}
            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-4">
                <div className="flex flex-wrap gap-3">
                    <button
                        className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'transfer'
                                ? 'bg-blue-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            }`}
                        onClick={() => switchOperation('transfer')}
                    >
                        <ArrowLeftRight size={18} />
                        Transfer Money
                    </button>
                    <button
                        className={`flex items-center gap-2 px-6 py-3 rounded-lg font-medium transition ${activeOperation === 'export'
                                ? 'bg-blue-600 text-white shadow-md'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            }`}
                        onClick={() => switchOperation('export')}
                    >
                        <Download size={18} />
                        Export Transactions (CSV)
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
                {/* Transfer Money */}
                {activeOperation === 'transfer' && (
                    <TransferForm onTransferSuccess={handleTransferSuccess} />
                )}

                {/* Export CSV */}
                {activeOperation === 'export' && (
                    <div className="bg-white rounded-lg shadow-md border border-gray-200 p-8">
                        <div className="max-w-3xl mx-auto">
                            <div className="mb-6">
                                <h3 className="text-xl font-semibold text-gray-900">Export Transaction History</h3>
                                <p className="text-gray-500 text-sm mt-1">Enter one of your account numbers to download its transaction history as a CSV file</p>
                            </div>

                            <ExportCSVButton />
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default UserTransactionsPage;