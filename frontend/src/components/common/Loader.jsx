import React from 'react';

const Loader = () => {
    return (
        <div className="flex justify-center items-center min-h-screen">
            <style>
                {`
                .spinner {
                    border: 3px solid #e5e7eb;
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                    border-left-color: #3b82f6;
                    animation: spin 0.8s linear infinite;
                }
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                `}
            </style>
            <div className="spinner"></div>
        </div>
    );
};
export default Loader;