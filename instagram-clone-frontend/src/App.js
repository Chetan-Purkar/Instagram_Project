import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Logout from "./pages/Logout";
import Home from "./pages/Home";
import { AuthProvider } from "./context/AuthContext";
import ThemeProvider from "./context/ThemeContext";
import Profile from "./pages/Profile";
import Profiles from "./pages/Profiles";
import CreatePost from "./pages/CreatePost";
import Update from "./components/settings/UpdateProfile";
import AccountSettings from "./components/settings/AccountSettings";
import Sidebar from "./components/Navigation/Sidebar";
import SearchPage from "./pages/SearchPage";
import MessagesPage from "./pages/MessagesPage";
import ChatUsersPage from "./pages/ChatUsersPage";
import TopNav from "./components/Navigation/TopNav";
import BottomNav from "./components/Navigation/BottomNav";
import CreateStory from "./components/Story/CreateStory";
import SearchSong from "./components/song/SearchSong";
import NotificationsPage from "./pages/Notifications";

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Router>
          <div className="flex flex-col h-screen">
          {/* Top Navigation (Mobile) */}
          <div className="md:hidden">
            <TopNav />
          </div>

          <div className="flex flex-1 overflow-hidden">
            {/* Sidebar (Desktop only) */}
            <div className="hidden md:block w-64">
              <Sidebar />
            </div>

            {/* Main Content */}
            <div className="flex-1 overflow-y-auto bg-gray-900 border-l border-gray-700">
              <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/logout" element={<Logout />} />
                <Route path="/home" element={<Home />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/create" element={<CreatePost />} />
                <Route path="/createStory" element={<CreateStory />} />
                <Route path="/notifications" element={<NotificationsPage />} />
                <Route path="/search" element={<SearchPage />} />
                <Route path="/update" element={<Update />} />
                <Route path="/:username" element={<Profiles />} />
                <Route path="/chats" element={<ChatUsersPage />} />
                <Route path="/chat/:userId" element={<MessagesPage />} />
                <Route path="/chat/user/:userId" element={<MessagesPage />} />
                <Route path="/settings" element={<AccountSettings jwtToken={localStorage.getItem("token")} />} />
                <Route path="/search/songs" element={<SearchSong />} />
              </Routes>
            </div>
          </div>

          {/* Bottom Navigation (Mobile) */}
          <div className="md:hidden">
            <BottomNav />
          </div>
        </div>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
