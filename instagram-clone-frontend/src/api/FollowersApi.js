import axios from "axios";

// Base API URL
const API_BASE_URL = "http://localhost:8080/api/followers";

// Toggle follow/unfollow (handles public/private accounts automatically)
export const toggleFollow = async (username, followingUsername) => {
  const token = localStorage.getItem("token");
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
  return response.data; // should now return { status: "PENDING" | "ACCEPTED" | "UNFOLLOWED" }
};


// Check follow status of current user → target user
export const getFollowStatus = async (currentUsername, targetUsername) => {
  const token = localStorage.getItem("token");
  const response = await axios.get(
    `${API_BASE_URL}/status/${targetUsername}?currentUsername=${currentUsername}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return response.data; // { status: "PENDING" | "ACCEPTED" | "NONE" }
};

// ✅ Get pending requests
export const getPendingRequests = async (username) => {
  const token = localStorage.getItem("token");
  const response = await axios.get(
    `${API_BASE_URL}/requests/pending/${username}`,
    {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    }
  );
  return response.data;
};


// ✅ Accept follow request (for private accounts)
export const acceptFollowRequest = async (followerUsername, targetUsername) => {
  const token = localStorage.getItem("token");
  const response = await axios.post(
    `${API_BASE_URL}/accept/${followerUsername}?targetUsername=${targetUsername}`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return response.data; // { status: "ACCEPTED" }
};

// ❌ Reject follow request (for private accounts)
export const rejectFollowRequest = async (followerUsername, targetUsername) => {
  const token = localStorage.getItem("token");
  const response = await axios.post(
    `${API_BASE_URL}/reject/${followerUsername}?targetUsername=${targetUsername}`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return response.data; // { status: "REJECTED" }
};

// Get followers of a user
export const getFollowers = async (username) => {
  const token = localStorage.getItem("token");
  const response = await axios.get(`${API_BASE_URL}/followers/${username}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
  return response.data; // include followStatus in each follower object
};

// Get following list of a user
export const getFollowing = async (username) => {
  const token = localStorage.getItem("token");
  const response = await axios.get(`${API_BASE_URL}/following/${username}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
  return response.data; // include followStatus in each following object
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

    return response.data; // should also include status field
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
