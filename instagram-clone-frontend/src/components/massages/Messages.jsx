import { useEffect, useState, useCallback, useContext, useRef } from "react";
import { useParams, Link } from "react-router-dom";
import { ThemeContext } from "../../context/ThemeContext";
import { getChatWithUser, getUserById, updateMessageStatus, deleteMessage } from "../../api/MessagesApi";
import { connectWebSocket, disconnectWebSocket, sendWebSocketMessage } from "../../utils/websocket";

const Messages = () => {
  const { theme } = useContext(ThemeContext);
  const { userId } = useParams();
  const currentUserId = parseInt(localStorage.getItem("userId"));
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [chatUser, setChatUser] = useState(null);
  const [selectedMessages, setSelectedMessages] = useState([]); // ✅ selected messages
  const chatContainerRef = useRef(null);

  // Helper to update message status
  const markMessageStatus = async (msg, status) => {
    try {
      if (msg.status === status) return;
      await updateMessageStatus(msg.id, status);
      setMessages((prev) =>
        prev.map((m) => (m.id === msg.id ? { ...m, status } : m))
      );
    } catch (error) {
      console.error("Failed to update message status:", error);
    }
  };

  // Fetch messages
  const fetchMessages = useCallback(async () => {
    try {
      const data = await getChatWithUser(userId);
      setMessages(data);

      // Mark messages as DELIVERED
      data.forEach((msg) => {
        if (msg.receiverId === currentUserId && msg.status === "SENT") {
          markMessageStatus(msg, "DELIVERED");
        }
      });
    } catch (error) {
      console.error("Error fetching messages:", error);
    }
  }, [userId, currentUserId]);

  // Fetch chat user
  const fetchChatUser = useCallback(async () => {
    try {
      const user = await getUserById(userId);
      setChatUser(user);
    } catch (error) {
      console.error("Error fetching user:", error);
    }
  }, [userId]);

  // WebSocket setup
  useEffect(() => {
    fetchMessages();
    fetchChatUser();

    connectWebSocket(currentUserId, (receivedMessage) => {
      if (
        receivedMessage.senderId === parseInt(userId) ||
        receivedMessage.receiverId === parseInt(userId)
      ) {
        setMessages((prev) => [...prev, receivedMessage]);

        // Mark as DELIVERED if received from other user
        if (receivedMessage.receiverId === currentUserId && receivedMessage.status === "SENT") {
          markMessageStatus(receivedMessage, "DELIVERED");
        }
      }
    });

    return () => disconnectWebSocket();
  }, [userId, currentUserId, fetchMessages, fetchChatUser]);

  // Auto-scroll to bottom on new messages
  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }

    // Mark all visible messages as SEEN
    messages.forEach((msg) => {
      if (msg.receiverId === currentUserId && msg.status !== "SEEN") {
        markMessageStatus(msg, "SEEN");
      }
    });
  }, [messages, currentUserId]);

  // Send new message
  const handleSend = () => {
    if (!newMessage.trim()) return;

    const messageDTO = {
      senderId: currentUserId,
      receiverId: parseInt(userId),
      content: newMessage,
      timestamp: new Date().toISOString(),
    };

    setNewMessage("");
    sendWebSocketMessage(messageDTO);
  };

  // Toggle message selection
  const toggleSelectMessage = (id) => {
    setSelectedMessages((prev) =>
      prev.includes(id) ? prev.filter((msgId) => msgId !== id) : [...prev, id]
    );
  };

  // Delete selected messages
  const handleDeleteSelected = async () => {
    try {
      for (let id of selectedMessages) {
        await deleteMessage(id);
      }
      setMessages((prev) => prev.filter((msg) => !selectedMessages.includes(msg.id)));
      setSelectedMessages([]);
    } catch (error) {
      console.error("Failed to delete selected messages:", error);
    }
  };

  return (
    <div className={`max-w-2xl mx-auto h-screen p-4 flex flex-col ${theme === "dark" ? "bg-gray-500 text-white" : "bg-white text-gray-900"}`} 
       style={{ height: `calc(100vh - 128px)` }}> 
      {/* Chat header */}
      <div className="flex items-center justify-between mb-4 flex-shrink-0">
         <div className="flex items-center gap-4">
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
          <Link to={`/${chatUser?.username}`} className="text-xl font-bold hover:underline">
            Chat with {chatUser?.username}
          </Link>
        </div>

        {/* Delete selected messages button */}
        {selectedMessages.length > 0 && (
          <button
            onClick={handleDeleteSelected}
            className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
          >
            Delete {selectedMessages.length} selected
          </button>
        )}
      </div>

      {/* Messages container */}
       <div
          ref={chatContainerRef}
          className={`flex-1 overflow-y-auto border rounded p-4 shadow scrollbar-hide ${theme === "dark" ? "bg-gray-800" : "bg-white"} flex flex-col gap-2`}
        >
        {messages.length === 0 ? (
          <p className="text-center text-gray-400 mt-4">No messages yet. Start the conversation!</p>
        ) : (
          messages.map((msg, idx) => {
            const isSender = msg.senderId === currentUserId;
            const isSelected = selectedMessages.includes(msg.id);
            return (
              <div
                key={idx}
                className={`flex flex-col max-w-xs ${isSender ? "self-end" : "self-start"} cursor-pointer`}
                onClick={() => isSender && toggleSelectMessage(msg.id)}
              >
                <div className={` p-2 rounded ${isSender ? "bg-blue-600 text-white" : theme === "dark" ? "bg-gray-700 text-white" : "bg-gray-200 text-gray-900"} ${isSelected ? "ring-2 ring-red-400" : ""}`}>
                  <p>{msg.content}</p>
                  <small className="text-xs text-gray-400 block mt-1">{new Date(msg.timestamp).toLocaleString()}</small>
                  <small className={`${msg.status === "SEEN" ? "text-green-400" : "text-gray-300"} text-xs block mt-1`}>{msg.status}</small>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Input area */}
      <div className="mt-4 flex flex-shrink-0">
        <input
          type="text"
          className={`flex-grow border p-2 rounded-l focus:outline-none focus:ring-2 focus:border-transparent ${
            theme === "dark" ? "bg-gray-800 border-gray-700 text-gray-200 focus:ring-gray-600" : "bg-white border-gray-300 text-gray-900 focus:ring-blue-500"
          }`}
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault();
              handleSend();
            }
          }}
          placeholder="Type a message..."
        />
        <button
          onClick={handleSend}
          className={`px-4 rounded-r border focus:outline-none focus:ring-2 focus:border-transparent ${
            theme === "dark" ? "bg-gray-800 border-gray-700 text-white hover:bg-gray-700 focus:ring-gray-600" : "bg-blue-500 border-blue-500 text-white hover:bg-blue-600 focus:ring-blue-400"
          }`}
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default Messages;
