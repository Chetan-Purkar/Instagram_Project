
//file: src/pages/MessagesPage.jsx
import { useContext } from "react";
import Messages from "../components/massages/Messages";
import { ThemeContext } from "../context/ThemeContext";

const MessagesPage = () => {
  const { theme } = useContext(ThemeContext);
  return (
    <div className={`min-h-screen bg-gray-50 ${theme === "dark" ? "bg-gray-900 border border-gray-700" : "bg-gray-100 border border-gray-300"}`}>
      <Messages />
    </div>
  );
};

export default MessagesPage;
