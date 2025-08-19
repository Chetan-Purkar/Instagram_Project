import React from "react";

const ProfileCard = ({ username, profileImage, bio }) => {
  return (
    <div className="bg-white shadow-md rounded-lg p-4 flex flex-col items-center space-y-2 w-64">
      <img
        src={profileImage}
        alt={username}
        className="w-20 h-20 rounded-full object-cover border-2 border-gray-300"
      />
      <h2 className="text-lg font-semibold text-gray-800">{username}</h2>
      <p className="text-sm text-gray-600 text-center px-2">{bio}</p>
      <button className="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-600 transition">
        Follow
      </button>
    </div>
  );
};

export default ProfileCard;
