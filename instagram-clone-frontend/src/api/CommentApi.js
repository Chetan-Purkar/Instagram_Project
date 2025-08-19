// CommentApi.js
import axios from "axios";

const API_URL = "http://localhost:8080/api/comments";

// Fetch comments by post with token for authentication
const fetchComments = async (postId) => {
    try {
        const token = localStorage.getItem("token"); // Retrieve token from localStorage
        if (!token) {
            throw new Error("No authentication token found");
        }

        const response = await axios.get(`${API_URL}/posts/${postId}/comments`, {
            headers: {
                "Authorization": `Bearer ${token}` // Attach token for authentication
            },
            withCredentials: true // Ensure credentials are sent if required
        });

        return response.data; // Return the list of comments
    } catch (error) {
        console.error("Error fetching comments:", error.response ? error.response.data : error.message);
        return [];
    }
};

// Add Comment
const addComment = async (postId, username, text) => {
    try {
        const token = localStorage.getItem("token"); // Retrieve token from localStorage
        if (!token) {
            throw new Error("No authentication token found");
        }

        const response = await axios.post(
            `${API_URL}/add`,
            { postId, username, text },
            {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}` // Attach token for authentication
                },
                withCredentials: true // Ensure credentials are sent if required
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error adding comment:", error.response?.data || error.message);
        throw error;
    }
};

// Delete Comment
const deleteComment = async (commentId, username) => {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            throw new Error("No authentication token found");
        }

        const response = await axios.delete(
            `${API_URL}/delete/${commentId}?username=${encodeURIComponent(username)}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                },
                withCredentials: true
            }
        );

        if (response.status === 200) {
            alert("Comment deleted successfully");
            // Update UI or state here
        }
    } catch (error) {
        console.error("Delete comment error:", error.response?.data || error.message);
        alert(error.response?.data || "Error deleting comment");
    }
};


export { addComment, deleteComment, fetchComments };
