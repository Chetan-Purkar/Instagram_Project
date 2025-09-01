// src/api/MessagesApi.js
import axios from "axios";

// Create axios instance with base URL
const api = axios.create({
  baseURL: "http://localhost:8080/api/messages", // Update if your backend URL is different
});

// Add JWT token to headers automatically
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token"); // Assuming you store JWT in localStorage
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ------------------- 📌 Messages API ------------------- //

// 1️⃣ Send a message
export const sendMessage = async (receiverId, content) => {
  const response = await api.post("/send", { receiverId, content });
  return response.data;
};

// 2️⃣ Get conversation with a user
export const getChatWithUser = async (chatWithId) => {
  const response = await api.get("/chat", { params: { chatWithId } });
  return response.data;
};

// 3️⃣ Get all chat users
export const getAllChatUsers = async () => {
  const response = await api.get("/users");
  return response.data;
};

// 4️⃣ Get user details by ID
export const getUserById = async (userId) => {
  const response = await api.get(`/user/${userId}`);
  return response.data;
};

// 5️⃣ Update message status
export const updateMessageStatus = async (messageId, status) => {
  const response = await api.put(`/${messageId}/status`, null, {
    params: { status },
  });
  return response.data;
};

// 6️⃣ Soft delete message
export const deleteMessage = async (messageId) => {
  const response = await api.delete(`/${messageId}`);
  return response.data;
};
