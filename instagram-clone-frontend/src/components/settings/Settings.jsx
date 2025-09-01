import { useState, useContext } from "react";
import { ThemeContext } from "../../context/ThemeContext";
import AccountSettings from "./AccountSettings";
import AppSettings from "./AppSettings";

const Settings = () => {
  const { theme } = useContext(ThemeContext);
  const [activeTab, setActiveTab] = useState("account");

  return (
    <div className={`max-w-3xl mx-auto p-6 ${theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-gray-900"}`}>
      <h1 className="text-2xl font-bold mb-6">Settings</h1>

      {/* Tabs */}
      <div className="flex border-b mb-6">
        <button
          onClick={() => setActiveTab("account")}
          className={`py-2 px-4 font-medium ${
            activeTab === "account" ? "border-b-2 border-blue-500 text-blue-500" : "text-gray-500"
          }`}
        >
          Account
        </button>
        <button
          onClick={() => setActiveTab("app")}
          className={`ml-4 py-2 px-4 font-medium ${
            activeTab === "app" ? "border-b-2 border-blue-500 text-blue-500" : "text-gray-500"
          }`}
        >
          App
        </button>
      </div>

      {/* Render selected tab */}
      {activeTab === "account" && <AccountSettings />}
      {activeTab === "app" && <AppSettings />}
    </div>
  );
};

export default Settings;
