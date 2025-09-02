import axios from "axios";

// ✅ Create axios instance with base URL
const api = axios.create({
  baseURL: "http://localhost:8080/api/notifications", // Change if backend URL differs
});

// ✅ Add JWT token to headers automatically
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ------------------- 📌 Notifications API ------------------- //

// Helper to handle API calls with safe error logging
const handleRequest = async (requestFn, errorMsg, defaultValue = null) => {
  try {
    const response = await requestFn();
    return response.data;
  } catch (error) {
    console.error(`${errorMsg}:`, error.response?.data || error.message);
    return defaultValue;
  }
};

// 1️⃣ Get all notifications
export const getNotifications = () =>
  handleRequest(() => api.get("/all"), "Error fetching notifications", []);

// 2️⃣ Get unread notifications
export const getUnreadNotifications = () =>
  handleRequest(() => api.get("/unread"), "Error fetching unread notifications", []);

// 3️⃣ Mark a notification as read
export const markAsRead = (notificationId) =>
  handleRequest(() => api.put(`/${notificationId}/read`), `Error marking notification ${notificationId} as read`);

// 4️⃣ Accept a follow request
export const acceptFollowRequest = (followerId) =>
  handleRequest(() => api.put(`/follow/${followerId}/accept`), `Error accepting follow request ${followerId}`);

// 5️⃣ Reject a follow request
export const rejectFollowRequest = (followerId) =>
  handleRequest(() => api.put(`/follow/${followerId}/reject`), `Error rejecting follow request ${followerId}`);
