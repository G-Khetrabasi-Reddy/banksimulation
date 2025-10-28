import React from 'react';

const Modal = ({ children, onClose, title = "Details" }) => (
  <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center p-4">
    <div className="bg-white p-8 rounded-lg shadow-2xl relative w-full max-w-2xl max-h-[90vh] overflow-y-auto">
      <h2 className="text-2xl font-bold text-gray-800 mb-4">{title}</h2>
      <button
        onClick={onClose}
        className="absolute top-4 right-4 text-gray-500 hover:text-gray-800 text-3xl leading-none"
      >
        &times;
      </button>
      {children}
    </div>
  </div>
);

export default Modal;