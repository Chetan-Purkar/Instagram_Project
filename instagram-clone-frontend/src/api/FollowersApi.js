import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/followers";

// Helper: auth headers
const authHeaders = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem("token")}`,
  },
  withCredentials: true,
});

// ✅ Toggle follow/unfollow
export const toggleFollow = async (targetUsername) => {
  const response = await axios.post(
    `${API_BASE_URL}/toggle/${targetUsername}`,
    null,
    authHeaders()
  
  );
  return response.data; // { status: "ACCEPTED" | "PENDING" | "UNFOLLOWED" | "SELF" }
};

// ✅ Check follow status
export const getFollowStatus = async (targetUsername) => {
  const response = await axios.get(
    `${API_BASE_URL}/status/${targetUsername}`,
    authHeaders()
  );
  return response.data; // { status: "FOLLOW" | "PENDING" | "FOLLOWING" }
};


// ✅ Check if following
export const isFollowing = async (targetUsername) => {
  const response = await axios.get(
    `${API_BASE_URL}/is-following/${targetUsername}`,
    authHeaders()
  );
  return response.data; // { isFollowing: true/false }
};

// ✅ Get followers of a user
export const getFollowers = async (username) => {
  const response = await axios.get(
    `${API_BASE_URL}/${username}/followers`,
    authHeaders()
  );
  return response.data; // [ { username, status } ]
};

// ✅ Get following of a user
export const getFollowing = async (username) => {
  const response = await axios.get(
    `${API_BASE_URL}/${username}/following`,
    authHeaders()
  );
  return response.data; // [ { username, status } ]
};

// ✅ Get pending requests (for private accounts)
export const getPendingRequests = async (username) => {
  const response = await axios.get(
    `${API_BASE_URL}/${username}/requests/pending`,
    authHeaders()
  );
  return response.data; // [ { id, follower, status } ]
};

// ✅ Accept follow request
export const acceptFollowRequest = async (requestId) => {
  const response = await axios.put(
    `${API_BASE_URL}/requests/${requestId}/accept`,
    {},
    authHeaders()
  );
  return response.data; // { message: "Follow request accepted" }
};

// ✅ Reject follow request
export const rejectFollowRequest = async (requestId) => {
  const response = await axios.put(
    `${API_BASE_URL}/requests/${requestId}/reject`,
    {},
    authHeaders()
  );
  return response.data; // { message: "Follow request rejected" }
};

// 🔍 Search followers
export const searchFollowers = async (username) => {
  const response = await axios.get(`${API_BASE_URL}/search-followers`, {
    params: { username },
    ...authHeaders(),
  });
  return response.data; // [ { username, status } ]
};

// 🔍 Search following
export const searchFollowing = async (username) => {
  const response = await axios.get(`${API_BASE_URL}/search-following`, {
    params: { username },
    ...authHeaders(),
  });
  return response.data; // [ { username, status } ]
};
