import { Link, useLocation } from "react-router-dom";

const BottomNav = () => {
  const location = useLocation();

  const navItems = [
    { path: "/home", icon: "🏠" },
    { path: "/search", icon: "🔍" },
    { path: "/create", icon: "➕" },
    { path: "/profile", icon: "👤" },
  ];

  return (
    <div className="bg-gray-900 p-2 border-t border-gray-700 flex justify-around text-white">
      {navItems.map((item) => (
        <Link
          key={item.path}
          to={item.path}
          className={`text-2xl p-2 rounded-full ${
            location.pathname === item.path ? "bg-blue-600" : ""
          }`}
        >
          {item.icon}
        </Link>
      ))}
    </div>
  );
};

export default BottomNav;
