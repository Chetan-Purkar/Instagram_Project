import { useEffect, useState, useCallback } from "react";
import { fetchComments, addComment, deleteComment } from "../../api/CommentApi";
import { Link } from "react-router-dom";

const CommentBox = ({ postId, currentUser }) => {
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");
    const [expanded, setExpanded] = useState(false);
    const [loading, setLoading] = useState(false);
    

    const loadComments = useCallback(async () => {
        const data = await fetchComments(postId);
        setComments(data);
    }, [postId]);
    

    useEffect(() => {
        loadComments();
    }, [loadComments]);

    const handleCommentChange = (e) => {
        setNewComment(e.target.value);
    };

    const handleAddComment = async () => {
        const trimmed = newComment.trim();
        if (!postId || !currentUser || !trimmed) return;
    
        setLoading(true);
        try {
            const response = await addComment(postId, currentUser, trimmed);
            console.log("Comment added successfully:", response);
            setNewComment("");
            await loadComments();
        } catch (err) {
            console.error("Failed to add comment:", err?.response?.data || err.message || err);
            alert("Something went wrong while posting your comment.");
        } finally {
            setLoading(false);
        }
    };
    

    const handleDelete = async (commentId) => {
        try {
            await deleteComment(commentId, currentUser);
            console.log("Comment deleted successfully");
            await loadComments();
        } catch (err) {
            console.error("Failed to delete comment:", err);
        }
    };

    const toggleExpand = () => {
        setExpanded(!expanded);
    };

    return (
        // The container for the comment box
        <div className="flex flex-col h-full p-5 overflow-hidden">
            <h4 className="font-semibold mb-2">Comments</h4>

            {/* Scrollable comments area */}
            <div className="flex-1 overflow-y-auto scrollbar-hide mb-16">
                {comments.length === 0 ? (
                    <p className="text-gray-500">No comments yet.</p>
                ) : (
                    <ul className="space-y-2">
                        {comments.slice(0, expanded ? comments.length : 2).map((comment, idx) => (
                            <li key={idx} className="flex justify-between items-center">
                                <div className="flex flex-col space-y-1 space-x-4">
                                    <div className="flex items-center">
                                        {comment.profileImage ? (
                                            <img
                                                src={`data:image/jpeg;base64,${comment.profileImage}`}
                                                alt="profile"
                                                className="w-6 h-6 rounded-full object-cover"
                                            />
                                        ) : (
                                            <div className="w-6 h-6 bg-gray-300 rounded-full flex items-center justify-center text-sm text-white">
                                                {comment.username.charAt(0).toUpperCase()}
                                            </div>
                                        )}
                                        <Link to={`/${comment.username}`} className="p-1 text-blue-500">@{comment.username}</Link>
                                    </div>
                                        <p className="text-sm">{comment.text}</p>
                                </div>
                                {/* {comment.username === currentUser?.username && ( */}
                                {comment.username === currentUser && (
                                    <button
                                        className="text-red-500 text-xs ml-2"
                                        onClick={() => handleDelete(comment.id)}
                                    >
                                        Delete
                                    </button>
                                )}
                            </li>
                        ))}
                    </ul>
                )}
                {comments.length > 2 && (
                    <button onClick={toggleExpand} className="text-blue-500 text-sm mt-2">
                        {expanded ? "Show Less" : "Show More"}
                    </button>
                )}
            </div>

            {/* Fixed comment input box at the bottom */}
            <div className="flex border-t border-gray-300 pt-3 mt-3 absolute bottom-0 left-0 right-0 p-3">
                <input
                    type="text"
                    placeholder="Add a comment..."
                    value={newComment}
                    onChange={handleCommentChange}
                    className="flex-grow p-2 text-black rounded-md border mr-2"
                />
                <button
                    onClick={handleAddComment}
                    className="bg-blue-500 text-white px-4 py-2 rounded-md"
                    disabled={loading}
                >
                    {loading ? "Adding..." : "Post"}
                </button>
            </div>
        </div>
    );
};

export default CommentBox;
