// File: src/pages/ProfilePage.jsx
import React, { useEffect, useState, useContext, useRef } from "react";
import { ThemeContext } from "../context/ThemeContext";
import { getUserByUsername, getCurrentUser } from "../api/UserApi";
import { useParams, useNavigate } from "react-router-dom";
import moment from "moment";
import FollowStats from '../components/FollowStats';
import FollowButton from "../components/FollowButton";
import LikeButton from "../components/LikeButton";
import CommentBox from "../components/CommentBox";

const ProfilePage = () => {
    const { theme } = useContext(ThemeContext);
    const { username } = useParams();
    const [profileUser, setProfileUser] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);
    const storedUser = useRef(localStorage.getItem("username")).current;
    const [loading, setLoading] = useState(true);
    const [posts, setPosts] = useState([]);
    const [selectedPost, setSelectedPost] = useState(null);
    const [likesByPost, setLikesByPost] = useState({});
    const navigate = useNavigate();


    useEffect(() => {
        const fetchProfileData = async () => {
            try {
                const [profileData, currentData] = await Promise.all([
                    getUserByUsername(username),
                    getCurrentUser()
                ]);
                
                
    
                if (!profileData) {
                    setProfileUser(null);
                    setLoading(false);
                    return;
                }
    
                setProfileUser(profileData);
                setCurrentUser(currentData);
    
                const userPosts = (profileData.posts || []).sort(
                    (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
                );
                setPosts(userPosts || []);

                const likesData = {};
                userPosts.forEach((post) => {
                    likesData[post.id] = {
                        liked: post.likedByCurrentUser || false,
                        count: post.likesCount || 0,
                    };
                });
                setLikesByPost(likesData);
                setLoading(false);
            } catch (error) {
                console.error("Error loading profile:", error);
                setLoading(false);
            }
        };
    
        fetchProfileData();
    }, [username]);
    
    
    
    const timeAgo = (date) => moment(date).fromNow();

    if (loading) return <div className="text-center py-10">Loading...</div>;
    if (!profileUser) return <div className="text-center py-10">User not found.</div>;

    return (
        <>
            
            <div className={`w-full max-w-3xl mx-auto p-6 sm:p-8 rounded-lg shadow-xl transition-all duration-300
                ${theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900"}`}>

                <div className="flex flex-col sm:flex-row sm:items-center sm:space-x-8">
                    <img
                        src={
                            profileUser.profileImage
                                ? `data:image/png;base64,${profileUser.profileImage}`
                                : "https://via.placeholder.com/150"
                        }
                        alt="Profile"
                        className="w-28 h-28 sm:w-32 sm:h-32 rounded-full object-cover mb-4 sm:mb-0"
                    />

                    <div className="flex-1">
                        <div className="flex items-center justify-start space-x-4 mb-2">
                            <h2 className="text-2xl font-bold">{profileUser.name}</h2>

                            {currentUser && currentUser.username !== username && (
                                <FollowButton
                                    currentUsername={currentUser.username}
                                    targetUsername={username}
                                />
                            )}

                            <button
                                onClick={() => navigate(`/chat/user/${profileUser.id}`)}
                                className="px-4 py-1 rounded-md text-sm font-medium border bg-gray-200 hover:bg-gray-100 text-black"
                                >
                                Message
                            </button>

                        </div>

                        <p className="text-gray-500 text-sm">@{profileUser.username}</p>

                        <div className="flex space-x-3 mt-4 text-sm font-medium">
                            <span className="mr-2"><strong>{posts.length}</strong> Posts</span>
                            <FollowStats username={username} />
                        </div>


                        <div className="mt-3">
                            <p className={`text-sm ${theme === "dark" ? "text-gray-300" : "text-gray-700"}`}>
                                {profileUser.bio || "No bio yet."}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            <div className="flex justify-center mt-8 border-b-2 border-gray-200 dark:border-gray-700">
                <h2 className="text-xl font-semibold text-gray-800 dark:text-white">Posts</h2>
            </div>
            {posts && posts.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-1 mt-8 px-4 max-w-6xl mx-auto">
                {posts.map((post) => (
                <div
                    key={post.id}
                    onClick={() => setSelectedPost(post)}
                    className={`group relative cursor-pointer rounded-lg shadow-lg overflow-hidden transition duration-300 
                    ${theme === "dark" ? "bg-gray-800" : "bg-white hover:shadow-xl"}`}
                >
                    {/* Image Container */}
                    <div className="h-[70vh] flex items-center justify-center overflow-hidden">
                    <img
                        src={`data:${post.mediaType};base64,${post.mediaData}`}
                        alt="Post"
                        className="w-full h-full object-cover transition duration-300 group-hover:opacity-70"
                    />
                    </div>

                    {/* Overlay on Hover */}
                    <div className="absolute inset-0 bg-black bg-opacity-60 opacity-0 group-hover:opacity-100 transition duration-300 flex items-center justify-center">
                    <div onClick={(e) => e.stopPropagation()} className="flex text-white text-center">
                        <LikeButton
                        postId={post.id}
                        likeInfo={likesByPost[post.id]}
                        setLikesByPost={setLikesByPost}
                        />
                        <p className="text-lg font-semibold">💬 {post.commentsCount}</p>
                    </div>
                    </div>
                </div>
                ))}
            </div>
            ) : (
            <div className="text-center text-gray-500 mt-10">No posts available</div>
            )}

            {selectedPost && (
            <div
            className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-60 z-50"
            onClick={() => setSelectedPost(null)}
            >
            {/* Modal Content */}
            <div
                className="bg-white dark:bg-gray-900 rounded-lg shadow-xl flex flex-col md:flex-row w-full max-w-6xl h-[90vh] mx-4 overflow-hidden"
                onClick={(e) => e.stopPropagation()}
            >
                {/* Left: Post Image */}
                <div className="w-full md:w-1/2 flex items-center justify-center bg-black">
                    <div className="w-full h-full aspect-[4/5] md:aspect-auto">
                        <img
                            src={`data:${selectedPost.mediaType};base64,${selectedPost.mediaData}`}
                            alt="Post"
                            className="w-full h-full object-cover"
                        />
                    </div>
                </div>
        
                {/* Right: Info */}
                <div className="w-full md:w-1/2 relative p-6 flex flex-col justify-between overflow-y-auto">
                    {/* Close button */}
                    <button
                        onClick={() => setSelectedPost(null)}
                        className="absolute top-4 right-4 text-white px-2 rounded-full hover:bg-gray-600 transition"
                    >
                        ✕
                    </button>
        
                    <div className="space-y-4 mt-10">
                        <div className="flex items-center justify-between space-x-2">
                            <div className="relative flex items-center space-x-2">
                                <img
                                    src={
                                        profileUser.profileImage
                                            ? `data:image/png;base64,${profileUser.profileImage}`
                                            : "https://via.placeholder.com/150"
                                    }
                                    alt="Profile"
                                    className="w-8 h-8 rounded-full object-cover"
                                />
                                 <strong className="text-white">{profileUser.username}</strong>
                            </div>
                            <div className=" justify-between text-sm text-gray-400">
                                <span>{timeAgo(selectedPost.createdAt)}</span>
                            </div>
                        </div>
        
        
                        <h6 className="text-lg text-gray-800 dark:text-white">
                            {selectedPost.caption}
                        </h6>
        
                        <p className="flex items-center text-sm text-gray-500 dark:text-gray-400">
                            
                            {likesByPost[selectedPost.id] && (
                                <LikeButton
                                    postId={selectedPost.id}
                                    likeInfo={likesByPost[selectedPost.id]}
                                    setLikesByPost={setLikesByPost}
                                />
                            )}
                            <span className="mx-2">•</span>
                            <span>{selectedPost.commentsCount} Comments</span>
                        </p>
                        
        
                        <div className=" text-sm text-gray-700 dark:text-gray-300 h-[400px] overflow-hidden">
                            <div className="relative h-full">
                                <CommentBox postId={selectedPost?.id} currentUser={storedUser} />
                            </div>
                        </div>

                        
                    </div>
                </div>
            </div>
        </div>
        
        )}

        </>
    );
};

export default ProfilePage;
