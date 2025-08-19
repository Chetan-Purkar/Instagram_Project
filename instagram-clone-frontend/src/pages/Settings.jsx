import React, { useEffect, useState, useContext } from "react";
import { ThemeContext } from "../context/ThemeContext";
import { fetchAllPosts } from "../api/PostApi";
import { fetchComments, addComment } from "../api/CommentApi";
import { toggleLike } from "../api/LikesApi";
import moment from "moment";
import { Link } from "react-router-dom";
import LikeButton from "../components/LikeButton";

const Home = () => {
  const { theme } = useContext(ThemeContext);
  const [posts, setPosts] = useState([]);
  const [commentsByPost, setCommentsByPost] = useState({});
  const [expandedComments, setExpandedComments] = useState({});
  const [showCommentsPopup, setShowCommentsPopup] = useState(null);
  const [newComments, setNewComments] = useState({});
  const [loading, setLoading] = useState(false);
  const [likesByPost, setLikesByPost] = useState({});

  const currentUser = localStorage.getItem("username");

  useEffect(() => {
    const fetchData = async () => {
      const data = await fetchAllPosts();
      const likesData = {};
      const commentsData = {};

      await Promise.all(
        data.map(async (post) => {
          likesData[post.id] = {
            liked: post.likedByCurrentUser ?? post.likes?.includes(currentUser),
            count: post.likes?.length ?? post.likesCount ?? 0,
          };
          commentsData[post.id] = await fetchComments(post.id);
        })
      );

      setPosts(data);
      setLikesByPost(likesData);
      setCommentsByPost(commentsData);
    };

    fetchData();
  }, [currentUser]);

  const timeAgo = (date) => moment(date).fromNow();

  const toggleComments = (postId) => {
    setExpandedComments((prev) => ({ ...prev, [postId]: !prev[postId] }));
  };

  const toggleCommentsPopup = (postId) => {
    setShowCommentsPopup((prev) => (prev === postId ? null : postId));
  };

  const handleCommentChange = (postId, text) => {
    setNewComments((prev) => ({ ...prev, [postId]: text }));
  };

  const handleAddComment = async (postId) => {
    const commentText = newComments[postId]?.trim();
    if (!commentText) return;

    setLoading(true);
    try {
      await addComment(postId, currentUser, commentText);
      setNewComments((prev) => ({ ...prev, [postId]: "" }));
      const updatedComments = await fetchComments(postId);
      setCommentsByPost((prev) => ({ ...prev, [postId]: updatedComments }));
    } catch (error) {
      console.error("Error adding comment:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleLike = async (postId) => {
    const current = likesByPost[postId];
    const optimisticUpdate = {
      liked: !current?.liked,
      count: current?.liked ? current.count - 1 : current.count + 1,
    };

    setLikesByPost((prev) => ({ ...prev, [postId]: optimisticUpdate }));

    try {
      const result = await toggleLike(postId);
      if (!result) throw new Error("Failed to toggle like");
    } catch (error) {
      console.error("Toggle like failed:", error);
      setLikesByPost((prev) => ({ ...prev, [postId]: current }));
    }
  };

  return (
    <div
      className={`flex flex-col items-center min-h-screen p-4 transition-all duration-300 ${
        theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-black"
      }`}
    >
      <h2 className="text-3xl font-bold mb-6">Latest Posts</h2>

      {posts.length === 0 ? (
        <p className="text-gray-500">No posts found.</p>
      ) : (
        posts.map((post) => {
          const {
            id,
            username,
            createdAt,
            mediaData,
            mediaType,
            caption,
            commentsCount,
            profileImage,
          } = post;

          const likeInfo = likesByPost[id] ?? { liked: false, count: 0 };
          const comments = commentsByPost[id] ?? [];

          return (
            <div
              key={id}
              className={`w-full max-w-md p-5 mb-6 rounded-lg shadow-lg ${
                theme === "dark"
                  ? "bg-gray-800 border border-gray-700"
                  : "bg-gray-100 border border-gray-300"
              }`}
            >
              <div className="flex justify-between items-center mb-3">
                <div className="flex items-center space-x-3">
                  <img
                    src={
                      profileImage
                        ? `data:image/png;base64,${profileImage}`
                        : "https://via.placeholder.com/50"
                    }
                    alt="Profile"
                    className="w-10 h-10 rounded-full border border-gray-400"
                  />
                  <Link to={`/${username}`} className="font-semibold">
                    {username}
                  </Link>
                </div>
                <span className="text-sm text-gray-500">
                  {timeAgo(createdAt)}
                </span>
              </div>

              <div className="w-full mb-3">
                {mediaType?.includes("image") && (
                  <img
                    src={`data:${mediaType};base64,${mediaData}`}
                    alt="Post"
                    className="w-full rounded-md object-cover"
                  />
                )}
                {mediaType?.includes("video") && (
                  <video controls className="w-full rounded-md">
                    <source
                      src={`data:${mediaType};base64,${mediaData}`}
                      type={mediaType}
                    />
                  </video>
                )}
              </div>

              <p>{caption}</p>

              <div className="flex justify-between mt-3 text-sm text-blue-500">
                <LikeButton
                  liked={likeInfo.liked}
                  count={likeInfo.count}
                  onToggle={() => handleToggleLike(id)}
                />
                <button onClick={() => toggleCommentsPopup(id)}>
                  {commentsCount} Comments
                </button>
                <button>Share</button>
              </div>

              {showCommentsPopup === id && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                  <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg w-full max-w-md max-h-[70vh] flex flex-col relative overflow-hidden">
                    <button
                      onClick={() => setShowCommentsPopup(null)}
                      className="absolute top-2 right-3 text-red-500 text-xl"
                    >
                      ×
                    </button>

                    <div className="p-5 overflow-y-auto scrollbar-hide flex-1">
                      <h4 className="font-semibold mb-2">Comments</h4>
                      {comments.length === 0 ? (
                        <p className="text-gray-500">No comments yet.</p>
                      ) : (
                        <ul className="space-y-2">
                          {comments
                            .slice(
                              0,
                              expandedComments[id]
                                ? comments.length
                                : 2
                            )
                            .map((comment, idx) => (
                              <li key={idx}>
                                <Link
                                  to={`/${comment.username}`}
                                  className="text-blue-500"
                                >
                                  @{comment.username}
                                </Link>
                                <p className="text-sm">{comment.text}</p>
                              </li>
                            ))}
                        </ul>
                      )}
                      {comments.length > 2 && (
                        <button
                          onClick={() => toggleComments(id)}
                          className="text-blue-500 text-sm mt-2"
                        >
                          {expandedComments[id]
                            ? "Show Less"
                            : "Show More"}
                        </button>
                      )}
                    </div>

                    <div className="flex border-t border-gray-300 p-3">
                      <input
                        type="text"
                        placeholder="Add a comment..."
                        value={newComments[id] ?? ""}
                        onChange={(e) =>
                          handleCommentChange(id, e.target.value)
                        }
                        className="flex-grow p-2 text-black rounded-md border mr-2"
                      />
                      <button
                        onClick={() => handleAddComment(id)}
                        className="bg-blue-500 text-white px-4 py-2 rounded-md"
                        disabled={loading}
                      >
                        {loading ? "Adding..." : "Post"}
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          );
        })
      )}
    </div>
  );
};

export default Home;
