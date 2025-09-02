import axios from "axios";

const API_URL = "http://localhost:8080/api/posts";

// Fetch paginated posts
export const fetchPosts = async (page = 0, size = 10) => {
    try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`${API_URL}/all`, {
            headers: {
                Authorization: token ? `Bearer ${token}` : "",
            },
            withCredentials: true,
            params: { page, size }, // ✅ Pagination parameters
        });

        console.log("Fetched posts:", response.data);
        // response.data is a Page<PostDTO>
        const { content, totalPages, totalElements, number: currentPage } = response.data;

        return {
            posts: content,
            totalPages,
            totalPosts: totalElements,
            currentPage,
        };
    } catch (error) {
        console.error(
            "❌ Error fetching posts:",
            error.response ? error.response.data : error.message
        );
        return {
            posts: [],
            totalPages: 0,
            totalPosts: 0,
            currentPage: 0,
        };
    }
};

// Create a new post
export const createPost = async (formData) => {
    try {
        const token = localStorage.getItem("token");
        const response = await axios.post(`${API_URL}/create`, formData, {
            headers: {
                "Content-Type": "multipart/form-data",
                Authorization: token ? `Bearer ${token}` : "",
            },
            withCredentials: true,
        });
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : "Error uploading post";
    }
};

// Delete a post by ID
export const deletePost = async (postId) => {
    try {
        const token = localStorage.getItem("token");
        await axios.delete(`${API_URL}/delete/${postId}`, {
            headers: {
                Authorization: token ? `Bearer ${token}` : "",
            },
            withCredentials: true,
        });
        return true;
    } catch (error) {
        console.error(
            `❌ Error deleting post (ID: ${postId}):`,
            error.response ? error.response.data : error.message
        );
        return false;
    }
};
