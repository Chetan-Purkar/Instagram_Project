// File: src/components/Messages.jsx
import { useEffect, useState, useCallback, useContext } from "react";
import { useParams } from "react-router-dom";
import { ThemeContext } from "../context/ThemeContext";
import { Link } from "react-router-dom";
import {
  getChatWithUser,
  getUserById,
} from "../api/MessagesApi";
import {
  connectWebSocket,
  disconnectWebSocket,
  sendWebSocketMessage,
} from "../utils/websocket";

const Messages = () => {
  const { theme } = useContext(ThemeContext);
  const { userId } = useParams();
  const currentUserId = parseInt(localStorage.getItem("userId"));
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [chatUser, setChatUser] = useState(null);

  const fetchMessages = useCallback(async () => {
    try {
      const data = await getChatWithUser(userId);
      setMessages(data);
    } catch (error) {
      console.error("Error fetching messages:", error);
    }
  }, [userId]);

  const fetchChatUser = useCallback(async () => {
    try {
      const user = await getUserById(userId);
      setChatUser(user);
    } catch (error) {
      console.error("Error fetching user:", error);
    }
  }, [userId]);

  useEffect(() => {
    fetchMessages();
    fetchChatUser();

    connectWebSocket(currentUserId, (receivedMessage) => {
      if (
        receivedMessage.senderId === parseInt(userId) ||
        receivedMessage.receiverId === parseInt(userId)
      ) {
        setMessages((prev) => [...prev, receivedMessage]);
      }
    });

    return () => {
      disconnectWebSocket();
    };
  }, [userId, fetchMessages, fetchChatUser, currentUserId]);

  const filteredMessages = messages.filter((msg, index, self) => {
  // In self-chat, show each message only once
  if (currentUserId === parseInt(userId)) {
    return msg.senderId === currentUserId && msg.receiverId === currentUserId;
  }
  // In normal chat, show all messages
  return true;
});


  const handleSend = () => {
    if (!newMessage.trim()) return;

    const messageDTO = {
      senderId: currentUserId,
      receiverId: parseInt(userId),
      content: newMessage,
    };

    setMessages((prev) => [
      ...prev,
    ]);
    setNewMessage("");
    sendWebSocketMessage(messageDTO);
  
  };

  useEffect(() => {
    const chatContainer = document.querySelector(".overflow-y-scroll");
    if (chatContainer) {
      chatContainer.scrollTop = chatContainer.scrollHeight;
    }
  }, [messages]);

  return (
    <div className={`max-w-2xl mx-auto p-4 scrollbar-hide ${theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900"}`}>
      <div className="flex items-center gap-4 mb-4">
        {chatUser?.profileImage ? (
          <img
            src={`data:image/jpeg;base64,${chatUser.profileImage}`}
            alt="Profile"
            className="w-12 h-12 rounded-full object-cover border border-gray-600"
          />
        ) : (
          <div className="w-12 h-12 rounded-full bg-gray-500 flex items-center justify-center text-white text-lg">
            {chatUser?.username?.charAt(0).toUpperCase()}
          </div>
        )}
        <Link to={`/${chatUser?.username}`} className="text-xl font-bold ">
          Chat with <span className="font-semibold hover:underline hover:text-blue-700">{chatUser?.username}</span>
        </Link>
      </div>

      <div className={`md:h-[600px] h-[450px] overflow-y-scroll border rounded scrollbar-hide ${theme === "dark" ? "bg-gray-800" : "bg-white"} p-6 shadow`}>
      {filteredMessages.map((msg, idx) => (
          <div key={idx} className="mb-2">
            <div
              className={` p-2 rounded ${
                msg.senderId === currentUserId
                  ? "bg-gray-900 text-right text-white"
                  : "bg-gray-900 text-left text-white"
              }`}
            >
              <p>{msg.content}</p>
              <small className="text-xs text-gray-500">
                {new Date(msg.timestamp).toLocaleString()}
              </small>
            </div>
          </div>
        ))}
      </div>
      <div className="mt-4 flex">
        <input
          type="text"
          className="flex-grow border border-gray-700 p-2 rounded-l bg-gray-800 text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-700 focus:border-transparent"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
           onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault();
              handleSend(); // your send function
            }
          }}
          placeholder="Type a message..."
        />
        <button
          onClick={handleSend}
          className="bg-gray-800 text-white px-4 rounded-r border border-gray-700 hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-700 focus:border-transparent"
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default Messages;
