import { Link } from "react-router-dom";

const TopNav = () => {
  return (
    <div className="bg-gray-900 p-4 flex justify-between items-center border-b border-gray-700">
      <h1 className="text-xl font-bold text-blue-400">Instagram</h1>
      <div className="gap-12 ">
        <Link to="/notifications" className="text-white text-2xl mr-4">🔔</Link> 
        <Link to="/chats" className="text-white text-2xl">💬</Link>
      </div>
    </div>
  );
};

export default TopNav;
