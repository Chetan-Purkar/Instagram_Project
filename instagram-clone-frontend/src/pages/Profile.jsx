import React, { useEffect, useState, useContext } from "react";
import { ThemeContext } from "../context/ThemeContext";
import { getCurrentUser } from "../api/UserApi";
import moment from "moment";
import { FaHeart, FaComment } from "react-icons/fa";

const Profile = () => {
    const { theme } = useContext(ThemeContext);
    const [user, setUser] = useState(null);
    const [selectedPost, setSelectedPost] = useState(null);

    useEffect(() => {
        const fetchUser = async () => {
            const userData = await getCurrentUser();
            setUser(userData);
        };
        fetchUser();
    }, []);

    const timeAgo = (date) => moment(date).fromNow();

    const handlePostClick = (post) => {
        setSelectedPost(post);
    };

    const closeModal = () => {
        setSelectedPost(null);
    };

    if (!user) return <p className="text-center text-gray-500">Loading...</p>;

    return (
        <div className={`w-1/2 mx-auto p-8 rounded-lg shadow-xl transition-all duration-300 
            ${theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900"}`}>

            {/* Profile Header */}
            <div className="flex items-center gap-6 p-6 shadow-lg rounded-lg">
                <img
                    src={user.profileImage ? `data:image/png;base64,${user.profileImage}` : "https://via.placeholder.com/150"}
                    alt="Profile"
                    className="w-28 h-28 rounded-full shadow-lg border-4 border-gray-300"
                />
                <div className="flex flex-col">
                    <p className="text-gray-500">@{user.username}</p>
                    <p className="text-gray-600 text-sm">{user.email}</p>
                    <p className="text-gray-400 mt-2">{user.bio || "No bio available"}</p>
                </div>
            </div>

            {/* Posts Section */}
            <div className="mt-8">
                <h3 className="text-xl font-semibold border-b pb-2">Recent Posts</h3>
                {user.posts.length > 0 ? (
                    <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                        {user.posts.map((post) => (
                            <div key={post.id}
                                onClick={() => handlePostClick(post)}
                                className={`cursor-pointer p-4 rounded-lg shadow-md hover:scale-105 transition transform 
                                    ${theme === "dark" ? "bg-gray-900 text-white" : "bg-gray-100 text-gray-900"}`}>
                                {post.mediaData && post.mediaType.includes("image") && (
                                    <img
                                        src={`data:${post.mediaType};base64,${post.mediaData}`}
                                        alt="Post"
                                        className="w-full h-40 object-cover rounded-md"
                                    />
                                )}
                                {post.mediaData && post.mediaType.includes("video") && (
                                    <video className="w-full h-40 object-cover rounded-md" muted>
                                        <source src={`data:${post.mediaType};base64,${post.mediaData}`} type={post.mediaType} />
                                    </video>
                                )}
                                <p className="mt-2 text-sm truncate">{post.caption}</p>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p className="text-gray-500 mt-3">No posts yet.</p>
                )}
            </div>

            {/* Modal Popup */}
            {selectedPost && (
                <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
                    <div className={`w-full max-w-2xl p-6 rounded-lg shadow-lg relative 
                        ${theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900"}`}>

                        <button
                            className="absolute top-2 right-3 text-xl font-bold"
                            onClick={closeModal}
                        >
                            &times;
                        </button>

                        <div className="mb-4 flex justify-between text-sm text-gray-400">
                            <h3 className="text-lg font-bold">{selectedPost.username}</h3>
                            <span>{timeAgo(selectedPost.createdAt)}</span>
                        </div>

                        {selectedPost.mediaData && selectedPost.mediaType.includes("image") && (
                            <img
                                src={`data:${selectedPost.mediaType};base64,${selectedPost.mediaData}`}
                                alt="Post"
                                className="w-full h-64 object-cover rounded-lg"
                            />
                        )}
                        {selectedPost.mediaData && selectedPost.mediaType.includes("video") && (
                            <video controls className="w-full h-64 rounded-lg mt-2">
                                <source src={`data:${selectedPost.mediaType};base64,${selectedPost.mediaData}`} type={selectedPost.mediaType} />
                            </video>
                        )}

                        <p className="mt-3">{selectedPost.caption}</p>

                        <div className="mt-3 flex items-center justify-between text-sm text-gray-500">
                            <div className="flex items-center gap-2">
                                <FaHeart className="text-red-500" />
                                <span>{selectedPost.likesCount} Likes</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <FaComment className="text-blue-400" />
                                <span>{selectedPost.commentsCount} Comments</span>
                            </div>
                        </div>

                        <div className="mt-4">
                            <h4 className="font-semibold">Comments:</h4>
                            {selectedPost.comments.length === 0 ? (
                                <p className="text-gray-500">No comments yet.</p>
                            ) : (
                                <ul className="mt-2 space-y-1 text-sm text-gray-300">
                                    {selectedPost.comments.map((comment, index) => (
                                        <li key={index} className="border-l-4 border-blue-400 pl-2">{comment}</li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Profile;
  