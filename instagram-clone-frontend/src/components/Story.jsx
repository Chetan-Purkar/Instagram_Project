import React from "react";

const Story = ({ username, profileImage }) => {
  return (
    <div className="flex flex-col items-center space-y-1">
      <div className="w-16 h-16 border-2 border-pink-500 rounded-full p-1">
        <img
          src={profileImage}
          alt={username}
          className="w-full h-full rounded-full object-cover"
        />
      </div>
      <p className="text-xs text-gray-700 font-medium truncate w-16 text-center">
        {username}
      </p>
    </div>
  );
};

export default Story;
