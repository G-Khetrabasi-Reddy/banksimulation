import React, { useState } from 'react';
import { performTransfer } from '../../api/transactionApi';

const TransferForm = ({ onTransferSuccess }) => {
    const [formData, setFormData] = useState({
        senderAccountNumber: '',
        receiverAccountNumber: '',
        amount: '',
        pin: '',
        description: '',
        transactionMode: 'ONLINE',
    });
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        setIsLoading(true);

        if (!formData.senderAccountNumber || !formData.receiverAccountNumber || !formData.amount || !formData.pin) {
            setError('Please fill in all required fields (Sender, Receiver, Amount, PIN).');
            setIsLoading(false);
            return;
        }
        if (parseFloat(formData.amount) <= 0) {
            setError('Amount must be positive.');
            setIsLoading(false);
            return;
        }
        if (formData.senderAccountNumber === formData.receiverAccountNumber) {
            setError('Sender and Receiver accounts cannot be the same.');
            setIsLoading(false);
            return;
        }

        try {
            const transferData = {
                ...formData,
                amount: parseFloat(formData.amount)
            };
            const response = await performTransfer(transferData);
            setSuccessMessage(response.message || 'Transfer successful!');
            setFormData({
                senderAccountNumber: '',
                receiverAccountNumber: '',
                amount: '',
                pin: '',
                description: '',
                transactionMode: 'ONLINE',
            });
            if (onTransferSuccess) {
                onTransferSuccess(response.transaction);
            }
        } catch (err) {
            console.error("Transfer error:", err.response?.data || err.message);
            setError(err.response?.data?.error || err.message || 'Transfer failed. Please check details and try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow-md border border-gray-200">
            <div className="mb-6">
                <h2 className="text-2xl font-semibold text-gray-900">Perform Money Transfer</h2>
                <p className="text-gray-500 text-sm mt-1">Transfer funds securely between accounts</p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Sender Account */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Sender Account Number *
                        </label>
                        <input
                            type="text"
                            name="senderAccountNumber"
                            value={formData.senderAccountNumber}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                            placeholder="Enter sender account"
                            required
                        />
                    </div>

                    {/* Receiver Account */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Receiver Account Number *
                        </label>
                        <input
                            type="text"
                            name="receiverAccountNumber"
                            value={formData.receiverAccountNumber}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                            placeholder="Enter receiver account"
                            required
                        />
                    </div>

                    {/* Amount */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Amount *
                        </label>
                        <div className="relative">
                            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-600 font-semibold">₹</span>
                            <input
                                type="number"
                                name="amount"
                                value={formData.amount}
                                onChange={handleChange}
                                min="0.01"
                                step="0.01"
                                className="w-full border border-gray-300 rounded-lg pl-8 pr-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                                placeholder="0.00"
                                required
                            />
                        </div>
                    </div>

                    {/* PIN */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Sender PIN *
                        </label>
                        <input
                            type="password"
                            name="pin"
                            value={formData.pin}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                            placeholder="Enter your PIN"
                            required
                        />
                    </div>
                </div>

                {/* Description */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Description (Optional)
                    </label>
                    <input
                        type="text"
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                        placeholder="Add a note for this transfer"
                        maxLength={100}
                    />
                </div>

                {/* Error & Success Messages */}
                {error && (
                    <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                        <p className="text-red-700 text-sm font-medium">{error}</p>
                    </div>
                )}
                {successMessage && (
                    <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                        <p className="text-green-700 text-sm font-medium">{successMessage}</p>
                    </div>
                )}

                {/* Submit Button */}
                <div className="pt-2">
                    <button
                        type="submit"
                        className="bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50 disabled:cursor-not-allowed shadow-sm w-full md:w-auto"
                        disabled={isLoading}
                    >
                        {isLoading ? 'Processing Transfer...' : 'Transfer Money'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default TransferForm;