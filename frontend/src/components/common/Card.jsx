import React from 'react';

/**
 * A reusable card component for consistent section styling.
 * @param {string} title - The main title of the card.
 * @param {string} description - A subtitle or description under the title.
 * @param {React.ReactNode} button - An optional button or element to render in the header.
 * @param {React.ReactNode} children - The content to render inside the card body.
 */
const Card = ({ title, description, button, children }) => {
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      {/* Card Header */}
      <div className="p-6 border-b border-gray-200 flex justify-between items-center">
        <div>
          <h2 className="text-xl font-bold text-gray-800">{title}</h2>
          {description && <p className="text-gray-600 mt-1">{description}</p>}
        </div>
        {button && <div>{button}</div>}
      </div>

      {/* Card Body */}
      <div className="p-6">
        {children}
      </div>
    </div>
  );
};

export default Card;