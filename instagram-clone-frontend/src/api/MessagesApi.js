const BASE_URL = "http://localhost:8080/api/messages";

// Helper to get JWT token from localStorage and construct headers
function getAuthHeaders() {
  const token = localStorage.getItem("token"); // or get from cookies if used
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
}

// 1. Send a message to a user
export async function sendMessage(receiverId, content) {
  try {
    const response = await fetch(`${BASE_URL}/send`, {
      method: "POST",
      headers: getAuthHeaders(),
      credentials: "include",
      body: JSON.stringify({ receiverId, content }),
    });

    if (!response.ok) throw new Error("Failed to send message");
    return await response.json();
  } catch (err) {
    console.error("Error sending message:", err);
    throw err;
  }
}

// 2. Get the chat conversation with a specific user
export async function getChatWithUser(chatWithId) {
  try {
    const response = await fetch(`${BASE_URL}/chat?chatWithId=${chatWithId}`, {
      method: "GET",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    if (!response.ok) throw new Error("Failed to fetch chat");
    return await response.json();
  } catch (err) {
    console.error("Error fetching chat with user:", err);
    throw err;
  }
}

// 3. Get all users the current user has chatted with
export async function getChatUsers() {
  try {
    const response = await fetch(`${BASE_URL}/users`, {
      method: "GET",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    if (!response.ok) throw new Error("Failed to fetch chat users");
    return await response.json();
  } catch (err) {
    console.error("Error fetching chat users:", err);
    throw err;
  }
}

// 1. Get user details by ID
export async function getUserById(userId) {
  try {
    const response = await fetch(`${BASE_URL}/user/${userId}`, {
      method: "GET",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    if (!response.ok) throw new Error("Failed to fetch user details");
    return await response.json();
  } catch (err) {
    console.error("Error fetching user details:", err);
    throw err;
  }
}