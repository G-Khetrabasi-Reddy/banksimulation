import React from 'react';

const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString('en-IN', {
            year: 'numeric', month: 'short', day: 'numeric',
            hour: 'numeric', minute: '2-digit', hour12: true
        });
    } catch (e) {
        return dateTimeString;
    }
};

const TransactionDetails = ({ transaction }) => {
    if (!transaction) {
        return (
            <div className="max-w-3xl mx-auto mt-6">
                <div className="bg-white rounded-lg shadow-md border border-gray-200 p-12 text-center">
                    <div className="text-gray-400 mb-3">
                        <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                        </svg>
                    </div>
                    <p className="text-gray-500 text-lg">No transaction details available</p>
                </div>
            </div>
        );
    }

    const {
        transactionId,
        senderAccountNumber,
        receiverAccountNumber,
        amount,
        transactionMode,
        status,
        transactionTime,
        description
    } = transaction;

    return (
        <div className="max-w-3xl mx-auto bg-white rounded-lg shadow-md overflow-hidden mt-6 border border-gray-200">
            <div className="bg-linear-to-r from-blue-500 to-blue-600 px-6 py-5">
                <h3 className="text-white text-xl font-semibold">Transaction Details</h3>
            </div>

            <div className="p-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="bg-gray-50 rounded-lg p-4">
                        <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Transaction ID</p>
                        <p className="text-gray-900 font-semibold text-lg wrap-break-word">{transactionId}</p>
                    </div>

                    <div className="bg-gray-50 rounded-lg p-4">
                        <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Date & Time</p>
                        <p className="text-gray-900 font-medium wrap-break-word">{formatDateTime(transactionTime)}</p>
                    </div>

                    <div className="bg-gray-50 rounded-lg p-4">
                        <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Sender Account</p>
                        <p className="text-gray-900 font-medium font-mono text-sm break-all">{senderAccountNumber}</p>
                    </div>

                    <div className="bg-gray-50 rounded-lg p-4">
                        <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Receiver Account</p>
                        <p className="text-gray-900 font-medium font-mono text-sm break-all">{receiverAccountNumber}</p>
                    </div>

                    <div className="bg-linear-to-br from-blue-50 to-blue-100 rounded-lg p-4 border border-blue-200">
                        <p className="text-xs text-blue-700 font-medium uppercase tracking-wide mb-1">Amount</p>
                        <p className="text-blue-700 font-bold text-2xl wrap-break-word">₹{amount?.toFixed(2)}</p>
                    </div>

                    <div className="bg-gray-50 rounded-lg p-4">
                        <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Transaction Mode</p>
                        <span className="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium bg-blue-100 text-blue-700 mt-1">
                            {transactionMode}
                        </span>
                    </div>

                    <div className="md:col-span-2 bg-gray-50 rounded-lg p-4">
                        <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Status</p>
                        <div className="flex items-center gap-2 mt-1">
                            <span
                                className={`inline-block w-2 h-2 rounded-full ${status?.toUpperCase() === 'SUCCESS' ? 'bg-green-500' : 'bg-red-500'
                                    }`}
                            ></span>
                            <p
                                className={`font-semibold ${status?.toUpperCase() === 'SUCCESS' ? 'text-green-600' : 'text-red-600'
                                    }`}
                            >
                                {status || 'N/A'}
                            </p>
                        </div>
                    </div>

                    {description && (
                        <div className="md:col-span-2 bg-gray-50 rounded-lg p-4">
                            <p className="text-xs text-gray-500 font-medium uppercase tracking-wide mb-1">Description</p>
                            <p className="text-gray-900 font-medium wrap-break-word">{description}</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TransactionDetails;