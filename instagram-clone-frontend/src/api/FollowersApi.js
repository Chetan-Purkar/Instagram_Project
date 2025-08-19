import axios from 'axios';

// Base API URL
const API_BASE_URL = 'http://localhost:8080/api/followers';

// Toggle follow/unfollow
export const toggleFollow = async (username, followingUsername) => {
  const token = localStorage.getItem('token');
  const response = await axios.post(
    `${API_BASE_URL}/toggle/${followingUsername}?username=${username}`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return response.data;
};

// Get followers of a user
export const getFollowers = async (username) => {
  const token = localStorage.getItem('token');
  const response = await axios.get(
    `${API_BASE_URL}/followers/${username}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return response.data;
};

// Get following list of a user
export const getFollowing = async (username) => {
  const token = localStorage.getItem('token');
  const response = await axios.get(
    `${API_BASE_URL}/following/${username}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return response.data;
};

// Check if current user is following another user
export const isFollowing = async (currentUsername, targetUsername) => {
    const token = localStorage.getItem('token');
    const response = await axios.get(
      `${API_BASE_URL}/is-following/${targetUsername}?currentUsername=${currentUsername}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      }
    );
    return response.data;
  };
  


// 🔍 Search followers by username
export const searchFollowers = async (username) => {
  try {
    const token = localStorage.getItem("token");

    const response = await axios.get(`${API_BASE_URL}/search-followers`, {
      params: { username },
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });

    return response.data;
  } catch (error) {
    console.error("Error searching followers:", error);
    throw error;
  }
};

// 🔍 Search following by username
export const searchFollowing = async (username) => {
  try {
    const token = localStorage.getItem("token");

    const response = await axios.get(`${API_BASE_URL}/search-following`, {
      params: { username },
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    });
    
    return response.data;
  } catch (error) {
    console.error("Error searching following:", error);
    throw error;
  }
};
