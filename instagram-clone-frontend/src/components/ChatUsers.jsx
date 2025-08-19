import React, { useEffect, useState, useContext } from "react";
import { getChatUsers } from "../api/MessagesApi";
import { useNavigate } from "react-router-dom";
import { ThemeContext } from "../context/ThemeContext";

const ChatUsers = () => {
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();
  const { theme } = useContext(ThemeContext);

  useEffect(() => {
    getChatUsers()
      .then(setUsers)
      .catch((err) => console.error("Failed to load chat users:", err));
  }, []);

  const handleClick = (userId) => {
    navigate(`/chat/${userId}`);
  };

  return (
    <div className={`p-6 max-w-md mx-auto ${theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900"}`}>
      <h2 className="text-2xl font-bold mb-4">Your Conversations</h2>
      <ul className={`rounded shadow ${theme === "dark" ? "bg-gray-800" : "bg-white"}`}>
        {users.map((user) => (
          <li
            key={user.id}
            onClick={() => handleClick(user.id)}
            className={`p-3 border-b cursor-pointer hover:bg-gray-900 ${theme === "dark" ? "border-gray-700" : "border-gray-300"}`}
          >
            {user.username}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ChatUsers;
