import React, { useEffect, useState, useContext } from "react";
import { useParams } from "react-router-dom";
import { ThemeContext } from "../context/ThemeContext";
import { getUserByUsername } from "../api/UserApi";
import { followUser, unfollowUser, checkFollowStatus } from "../api/FollowersApi";
import moment from "moment";
import { FaHeart, FaComment } from "react-icons/fa";

const Profiles = () => {
    const { theme } = useContext(ThemeContext);
    const { username } = useParams();
    const [user, setUser] = useState(null);
    const [isFollowing, setIsFollowing] = useState(false);
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const loggedInUser = localStorage.getItem("username");
    console.log(" User:", loggedInUser);
 
    
    
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const [userData, loggedInUser] = await Promise.all([
                    getUserByUsername(username),
                    JSON.parse(localStorage.getItem("user"))
                ]);

                console.log("✅ Logged In User:", loggedInUser?.username); 

                setUser(userData || { posts: [] });
                setCurrentUser(loggedInUser);

                if (loggedInUser?.username !== username) {
                    const status = await checkFollowStatus(username);
                    console.log("Follow status:", status);
                    setIsFollowing(status);
                }
            } catch (error) {
                console.error("❌ Error loading user data:", error);
                setUser({ posts: [] });
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, [username]);

    const handleFollowToggle = async () => {
        try {
            if (isFollowing) {
                await unfollowUser(username);
            } else {
                await followUser(username);
            }
            const updatedStatus = await checkFollowStatus(username);
            setIsFollowing(updatedStatus);
        } catch (error) {
            console.error("❌ Error toggling follow/unfollow:", error);
        }
    };

    const timeAgo = (date) => moment(date).fromNow();

    const renderMedia = (post) => {
        if (!post.mediaData) return null;

        if (post.mediaType.includes("image")) {
            return (
                <img
                    src={`data:${post.mediaType};base64,${post.mediaData}`}
                    alt="Post"
                    className="w-full h-64 object-cover mt-3 rounded-lg shadow-sm"
                />
            );
        }

        if (post.mediaType.includes("video")) {
            return (
                <video controls className="w-full h-64 mt-3 rounded-lg shadow-sm">
                    <source src={`data:${post.mediaType};base64,${post.mediaData}`} type={post.mediaType} />
                </video>
            );
        }

        return null;
    };

    const renderComments = (comments) => {
        if (!comments || comments.length === 0) {
            return <p className="text-gray-500">No comments yet.</p>;
        }

        return (
            <ul className="mt-2 space-y-1 text-sm text-gray-300">
                {comments.map((comment, idx) => (
                    <li key={idx} className="border-l-4 border-blue-400 pl-2">{comment}</li>
                ))}
            </ul>
        );
    };

    const bgClass = theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900";
    const postBgClass = theme === "dark" ? "bg-gray-800 text-white" : "bg-gray-100 text-gray-900";

    if (loading) {
        return <div className={`min-h-screen flex items-center justify-center ${bgClass}`}>Loading...</div>;
    }

    return (
        <div className={`flex flex-col items-center justify-center min-h-screen ${bgClass}`}>
            <div className="w-[50%] p-8 rounded-lg shadow-xl">
                <div className={`flex items-center gap-6 p-6 rounded-lg shadow-lg ${postBgClass}`}>
                    <img
                        src={user.profileImage ? `data:image/png;base64,${user.profileImage}` : "https://via.placeholder.com/150"}
                        alt="Profile"
                        className="w-28 h-28 rounded-full shadow-lg border-4 border-gray-300"
                    />
                    <div>
                        <div className="flex items-center gap-4 mb-2">
                            <h2 className="text-2xl font-bold">{user.name}</h2>
                            {!isOwnProfile && (
                                <button
                                    onClick={handleFollowToggle}
                                    className={`px-4 py-2 rounded-lg text-sm font-semibold transition duration-200 ${isFollowing ? "bg-red-500 hover:bg-red-600" : "bg-blue-500 hover:bg-blue-600"} text-white`}
                                >
                                    {isFollowing ? "Unfollow" : "Follow"}
                                </button>
                            )}
                            {!isOwnProfile && (
                                <button className="px-4 py-2 rounded-lg text-black text-sm font-semibold bg-gray-200 hover:bg-gray-300">
                                    Message
                                </button>
                            )}
                        </div>
                        <p className="text-gray-600 text-sm">{user.followersCount} Followers</p>
                        <p className="text-gray-500">@{user.username}</p>
                        <p className="text-gray-600 text-sm">{user.email}</p>
                        <p className="text-gray-400 mt-2">{user.bio || "No bio available"}</p>
                    </div>
                </div>

                {/* Posts Section */}
                <div className="mt-8">
                    <h3 className="text-xl font-semibold border-b pb-2">Recent Posts</h3>
                    {user.posts?.length > 0 ? (
                        <div className="mt-4 space-y-6">
                            {user.posts.map((post) => (
                                <div key={post.id} className={`p-6 rounded-lg shadow-md ${postBgClass}`}>
                                    <div className="flex justify-between items-center text-sm text-gray-400">
                                        <h3 className="font-semibold text-lg">{post.username}</h3>
                                        <span>{timeAgo(post.createdAt)}</span>
                                    </div>
                                    {renderMedia(post)}
                                    <p className="mt-3 text-gray-300">{post.caption}</p>
                                    <div className="mt-3 flex items-center justify-between text-sm text-gray-500">
                                        <div className="flex items-center gap-2">
                                            <FaHeart className="text-red-500" />
                                            <span>{post.likesCount} Likes</span>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <FaComment className="text-blue-400" />
                                            <span>{post.commentsCount} Comments</span>
                                        </div>
                                    </div>
                                    <div className="mt-4">
                                        <h4 className="font-semibold text-gray-400">Comments:</h4>
                                        {renderComments(post.comments)}
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p className="text-gray-500 mt-3">No posts yet.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Profiles;
