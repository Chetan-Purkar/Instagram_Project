import axios from "axios";

const API_URL = "http://localhost:8080/api/likes"; // Backend URL

export const toggleLike = async (postId) => {
  const token = localStorage.getItem("token");
  const username = localStorage.getItem("username"); // Get the username from localStorage (or sessionStorage)

  try {
    const response = await axios.post(`${API_URL}/toggle/${postId}`, null, {
      headers: {
        Authorization: token ? `Bearer ${token}` : "", // Ensure token is included
        "Content-Type": "application/json",
      },
      params: { username },
      withCredentials: true,
    });

    return response.data;
  } catch (error) {
    console.error("Error toggling like:", error);
    return null;
  }
};