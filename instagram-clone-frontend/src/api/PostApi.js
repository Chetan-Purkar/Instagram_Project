import axios from "axios";

const API_URL = "http://localhost:8080/api/posts";

// Fetch all posts
export const fetchAllPosts = async () => {
    try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`${API_URL}/all`, {
            headers: {
                Authorization: token ? `Bearer ${token}` : "",
            },
            withCredentials: true,
        });

        // ✅ Log like status for each post
        const posts = response.data;

        
        return posts;
    } catch (error) {
        console.error("❌ Error fetching posts:", error.response ? error.response.data : error.message);
        return [];
    }
};



export const createPost = async (formData, token) => {
  try {
    const response = await axios.post(`${API_URL}/create`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: `Bearer ${token}`, // Assuming JWT authentication
      },
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
        const response = await axios.delete(`${API_URL}/delete/${postId}`, {
            headers: {
                Authorization: token ? `Bearer ${token}` : "",
            },
            withCredentials: true,
        });
        console.log(response);
        return true;
    } catch (error) {
        console.error(`❌ Error deleting post (ID: ${postId}):`, error.response ? error.response.data : error.message);
        return false;
    }
};
