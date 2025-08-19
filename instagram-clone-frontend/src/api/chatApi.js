// chatApi.js - Handles chat-related API calls

const CHAT_API_URL = "http://localhost:8080/api/chats";

export const sendMessage = async (messageData, token) => {
  try {
    const response = await fetch(`${CHAT_API_URL}/send`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(messageData),
    });
    if (!response.ok) {
      throw new Error("Failed to send message");
    }
    return await response.json();
  } catch (error) {
    throw new Error(error.message);
  }
};

export const getMessages = async (chatId, token) => {
  try {
    const response = await fetch(`${CHAT_API_URL}/${chatId}`, {
      method: "GET",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) {
      throw new Error("Failed to fetch messages");
    }
    return await response.json();
  } catch (error) {
    throw new Error(error.message);
  }
};