import React, { useState } from 'react';
import { downloadTransactionsCSV } from '../../api/transactionApi';
import { Download } from 'lucide-react';

const ExportCSVButton = () => {
    const [accountNumber, setAccountNumber] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleDownload = async () => {
        setError('');
        if (!accountNumber) {
            setError('Please enter an account number.');
            return;
        }
        setIsLoading(true);
        try {
            await downloadTransactionsCSV(accountNumber);
            setAccountNumber('');
        } catch (err) {
            setError(err.message || 'Failed to download CSV.');
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto bg-white p-6 rounded-lg shadow-md border border-gray-200">
            <div className="mb-4">
                <h3 className="text-xl font-semibold text-gray-900">Export Transactions to CSV</h3>
                <p className="text-gray-500 text-sm mt-1">Download transaction history for a specific account</p>
            </div>

            <div className="flex flex-col sm:flex-row gap-3">
                <input
                    type="text"
                    placeholder="Enter Account Number"
                    value={accountNumber}
                    onChange={(e) => setAccountNumber(e.target.value)}
                    className="flex-1 border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                />
                <button
                    onClick={handleDownload}
                    className="bg-blue-600 text-white px-6 py-2.5 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm flex items-center justify-center gap-2"
                    disabled={isLoading}
                >
                    <Download size={18} />
                    {isLoading ? 'Downloading...' : 'Download CSV'}
                </button>
            </div>

            {error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-3 mt-4">
                    <p className="text-red-700 text-sm font-medium">{error}</p>
                </div>
            )}
        </div>
    );
};

export default ExportCSVButton;