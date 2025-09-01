import { Link, useLocation } from "react-router-dom";

const Sidebar = () => {
  const location = useLocation();

  const navItems = [
    { path: "/home", label: "Home", icon: "🏠" },
    { path: "/search", label: "Search", icon: "🔍" },
    { path: "/profile", label: "Profile", icon: "👤" },
    { path: "/notifications", label: "Notifications", icon: "🔔" },
    { path: "/create", label: "Create Post", icon: "➕" },
    { path: "/createStory", label: "Create Story", icon: "📖" },
    { path: "/chats", label: "Messages", icon: "💬" },
    { path: "/logout", label: "Logout", icon: "🚪", logout: true },
  ];

  return (
    <div className="hidden md:flex w-64 h-screen bg-gray-900 text-white p-5 shadow-lg flex-col">
      {/* Logo or Brand */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold tracking-wide text-blue-400">
          Instagram
        </h1>
        <p className="text-sm text-gray-400">Connect with friends and the world around you.</p>
      </div>

      {/* Navigation */}
      <nav className="flex-1">
        <ul className="space-y-2">
          {navItems.map((item) => (
            <li key={item.path}>
              <Link
                to={item.path}
                className={`flex items-center gap-3 p-3 rounded-lg transition-colors ${
                  location.pathname === item.path
                    ? "bg-blue-600"
                    : "hover:bg-gray-700"
                }`}
              >
                <span className="text-lg">{item.icon}</span>
                <span className="text-md">{item.label}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      {/* Footer (Optional) */}
      <div className="mt-auto text-xs text-gray-500">
        © {new Date().getFullYear()} Instagram
      </div>
    </div>
  );
};

export default Sidebar;
