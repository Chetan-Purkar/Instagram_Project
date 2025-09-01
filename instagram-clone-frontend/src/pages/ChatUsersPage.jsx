import {useContext} from "react";
import ChatUsers from "../components/massages/ChatUsers";
import {ThemeContext} from "../context/ThemeContext";

const ChatUsersPage = () => {
  const { theme } = useContext(ThemeContext);
  return (
    <div className={`min-h-screen p-4 ${theme === "dark" ? "bg-gray-900" : "bg-gray-50"}`}>
      <ChatUsers />
    </div>
  );
};

export default ChatUsersPage;
