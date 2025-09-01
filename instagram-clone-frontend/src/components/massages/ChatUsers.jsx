import { useEffect, useState, useContext } from "react";
import { getAllChatUsers } from "../../api/MessagesApi";
import { useNavigate } from "react-router-dom";
import { ThemeContext } from "../../context/ThemeContext";

const ChatUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const { theme } = useContext(ThemeContext);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getAllChatUsers();
        setUsers(data);
      } catch (err) {
        console.error("Failed to load chat users:", err);
        setError("Failed to load conversations. Try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const handleClick = (userId) => {
    navigate(`/chat/${userId}`);
  };

  const UserItem = ({ user }) => (
    <li
      key={user.id}
      onClick={() => handleClick(user.id)}
      className={`flex items-center p-3 border-b cursor-pointer transition-colors duration-200 ${
        theme === "dark"
          ? "border-gray-700 hover:bg-gray-700"
          : "border-gray-300 hover:bg-gray-100"
      }`}
    >
      {user.profileImage && (
        <img
          src={user.profileImage}
          alt={user.username}
          className="w-10 h-10 rounded-full mr-3 object-cover"
        />
      )}
      <span className="font-medium">{user.username}</span>
    </li>
  );

  return (
    <div
      className={`p-6 max-w-md mx-auto rounded-lg shadow ${
        theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900"
      }`}
    >
      <h2 className="text-2xl font-bold mb-4">Your Conversations</h2>

      {loading ? (
        <p className="text-center text-gray-500">Loading conversations...</p>
      ) : error ? (
        <p className="text-center text-red-500">{error}</p>
      ) : users.length === 0 ? (
        <p className="text-center text-gray-500">No conversations yet</p>
      ) : (
        <ul
          className={`rounded border overflow-hidden ${
            theme === "dark" ? "border-gray-700 bg-gray-800" : "border-gray-300 bg-white"
          }`}
        >
          {users.map((user) => (
            <UserItem key={user.id} user={user} />
          ))}
        </ul>
      )}
    </div>
  );
};

export default ChatUsers;
