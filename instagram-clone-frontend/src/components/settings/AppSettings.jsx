import { useContext, useState } from "react";
import { ThemeContext } from "../../context/ThemeContext";

const AppSettings = () => {
  const { theme, toggleTheme } = useContext(ThemeContext);
  const [notifications, setNotifications] = useState(true);
  const [autoPlay, setAutoPlay] = useState(false);

  const handleSave = () => {
    alert("App settings saved!");
    // You can integrate API here to persist these settings
  };

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold">App Settings</h2>
      <div className="flex items-center justify-between">
        <span>Dark Mode</span>
        <input type="checkbox" checked={theme === "dark"} onChange={toggleTheme} />
      </div>
      <div className="flex items-center justify-between">
        <span>Notifications</span>
        <input type="checkbox" checked={notifications} onChange={() => setNotifications(!notifications)} />
      </div>
      <div className="flex items-center justify-between">
        <span>Auto-play Videos</span>
        <input type="checkbox" checked={autoPlay} onChange={() => setAutoPlay(!autoPlay)} />
      </div>
      <button className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600" onClick={handleSave}>
        Save Settings
      </button>
    </div>
  );
};

export default AppSettings;
