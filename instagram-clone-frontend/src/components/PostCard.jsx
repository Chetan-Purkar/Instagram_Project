import React from "react";

const PostCard = ({ post }) => {
  return (
    <div className="bg-white shadow-lg rounded-xl p-4 mb-6">
      <div className="flex items-center gap-3 mb-2">
        <img
          src={post.userAvatar}
          alt="User Avatar"
          className="w-10 h-10 rounded-full"
        />
        <h3 className="font-semibold">{post.username}</h3>
      </div>
      <img
        src={post.image}
        alt="Post"
        className="w-full h-60 object-cover rounded-md"
      />
      <div className="mt-3">
        <p className="text-gray-800 font-medium">{post.caption}</p>
        <div className="flex justify-between items-center mt-2">
          <button className="text-gray-600 hover:text-red-500">❤️ {post.likes}</button>
          <button className="text-gray-600 hover:text-blue-500">💬 {post.comments}</button>
        </div>
      </div>
    </div>
  );
};

export default PostCard;
