// src/api/storyApi.js
import axios from "axios";

const API_URL = "http://localhost:8080/api/stories";

// Helper to set Authorization header
const getAuthHeader = (token) => ({
  headers: {
    Authorization: `Bearer ${token}`,
  },
});

// ✅ Create a new story
export const createStory = async ({ mediaFile, audioFile, caption, durationInHours }, token) => {
  try {
    const formData = new FormData();
    formData.append("mediaFile", mediaFile);
    if (audioFile) formData.append("audioFile", audioFile);
    if (caption) formData.append("caption", caption);
    formData.append("durationInHours", durationInHours || 24);

    const response = await axios.post(`${API_URL}/create`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: `Bearer ${token}`, // JWT token added here
      },
    });

    return response.data;
  } catch (error) {
    console.error("Error creating story:", error.response?.data || error.message);
    throw error.response ? error.response.data : "Error uploading story";
  }
};

// Get stories of the authenticated user
export const getMyStories = async (token) => {
  try {
    const response = await axios.get(`${API_URL}/me`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching my stories:", error.response?.data || error.message);
    throw error.response ? error.response.data : "Error fetching my stories";
  }
};

// Get stories of a specific user by username
export const getUserStories = async (username, token) => {
  try {
    const response = await axios.get(`${API_URL}/user/${username}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error(`Error fetching stories of ${username}:`, error.response?.data || error.message);
    throw error.response ? error.response.data : "Error fetching user stories";
  }
};

// Get stories of users followed by the authenticated user
export const getFollowingStories = async (token) => {
  try {
    const response = await axios.get(`${API_URL}/following`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching following stories:", error.response?.data || error.message);
    throw error.response ? error.response.data : "Error fetching following stories";
  }
};

// ✅ Delete expired stories
export const cleanupExpiredStories = async (token) => {
  try {
    const response = await axios.delete(`${API_URL}/cleanup`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || "Error cleaning up stories";
  }
};


// ✅ Add a reply to a story (authenticated)
const STORY_REPLY_URL = "http://localhost:8080/api/story-replies"; // note 'story-replies'


// ✅ Add a reply to a story
export const addStoryReply = async (storyId, replyMessage, token) => {
  try {
    const response = await axios.post(
      `${STORY_REPLY_URL}/${storyId}/add`,
      null,
      {
        params: { replyMessage },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error adding story reply";
  }
};

// ✅ Fetch replies for a story
export const getStoryReplies = async (storyId, token) => {
  try {
    const response = await axios.get(`${STORY_REPLY_URL}/${storyId}`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error fetching story replies";
  }
};

// ✅ Fetch all replies made by the authenticated user
export const getMyStoryReplies = async (token) => {
  try {
    const response = await axios.get(`${STORY_REPLY_URL}/my-replies`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error fetching my story replies";
  }
};

// ✅ Get reply count for a story
export const getStoryReplyCount = async (storyId, token) => {
  try {
    const response = await axios.get(`${STORY_REPLY_URL}/${storyId}/count`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error fetching story reply count";
  }
};


const STORY_VIEW_URL = "http://localhost:8080/api/story-views";

// Add a view for a story
export const addStoryView = async (storyId, token) => {
  try {
    const response = await axios.post(`${STORY_VIEW_URL}/${storyId}`, null, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error adding story view";
  }
};

// Get all viewers of a story
export const getStoryViews = async (storyId, token) => {
  try {
    const response = await axios.get(`${STORY_VIEW_URL}/${storyId}/all`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error fetching story views";
  }
};

// Get view count of a story
export const getStoryViewCount = async (storyId, token) => {
  try {
    const response = await axios.get(`${STORY_VIEW_URL}/${storyId}/count`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error fetching story view count";
  }
};



const STORY_LIKE_URL = "http://localhost:8080/api/stories/likes";


// Like a story
export const likeStory = async (storyId, token) => {
  try {
    const response = await axios.post(`${STORY_LIKE_URL}/${storyId}`, null, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error liking story";
  }
};

// Unlike a story
export const unlikeStory = async (storyId, token) => {
  try {
    const response = await axios.delete(`${STORY_LIKE_URL}/${storyId}`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error unliking story";
  }
};

// Get likes for a story
export const getStoryLikes = async (storyId, token) => {
  try {
    const response = await axios.get(`${STORY_LIKE_URL}/${storyId}`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error fetching story likes";
  }
};

// Get like count
export const getStoryLikeCount = async (storyId, token) => {
  try {
    const response = await axios.get(`${STORY_LIKE_URL}/${storyId}/count`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message || "Error fetching story like count";
  }
};
