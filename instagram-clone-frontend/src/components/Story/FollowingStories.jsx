import { useEffect, useState, useCallback } from "react";
import moment from "moment";
import { getFollowingStories } from "../../api/StoryApi";
import StoryLikes from "./StoryLikes";
import StoryReplies from "./StoryReplies";
import StoryViews from "./StoryViews";
import AudioPlayer from "../AudioPlayer";
import { X, ChevronLeft, ChevronRight } from "lucide-react";

const FollowingStories = () => {
  const [stories, setStories] = useState({});
  const [loading, setLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [muted, setMuted] = useState(false); // global mute for audio

  // Fetch stories once
  useEffect(() => {
    const fetchStories = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("token");
        const data = await getFollowingStories(token);

        // Group stories by username
        const grouped = data.reduce((acc, story) => {
          if (!acc[story.username]) acc[story.username] = [];
          acc[story.username].push(story);
          return acc;
        }, {});
        setStories(grouped);
      } catch (err) {
        console.error("Error fetching following stories:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchStories();
  }, []);

  const openUserStories = (username) => {
    setSelectedUser(username);
    setCurrentIndex(0);
  };

  const closeModal = useCallback(() => {
    setSelectedUser(null);
    setCurrentIndex(0);
  }, []);

  const nextStory = useCallback(() => {
    if (selectedUser && currentIndex < stories[selectedUser].length - 1) {
      setCurrentIndex((i) => i + 1);
    } else {
      closeModal();
    }
  }, [selectedUser, currentIndex, stories, closeModal]);

  const prevStory = useCallback(() => {
    if (selectedUser && currentIndex > 0) setCurrentIndex((i) => i - 1);
  }, [selectedUser, currentIndex]);

  // Keyboard navigation
  useEffect(() => {
    const handleKey = (e) => {
      if (e.key === "ArrowRight") nextStory();
      if (e.key === "ArrowLeft") prevStory();
      if (e.key === "Escape") closeModal();
    };
    window.addEventListener("keydown", handleKey);
    return () => window.removeEventListener("keydown", handleKey);
  }, [nextStory, prevStory, closeModal]);

  if (loading) return <p className="p-4">Loading following users...</p>;
  if (!stories || Object.keys(stories).length === 0)
    return <p className="p-4">No active stories from following users.</p>;

  return (
    <div className="p-6">
      {/* Horizontal Avatars */}
      <div className="flex space-x-4 overflow-x-auto scrollbar-hide pb-2">
        {Object.keys(stories).map((username) => {
          const userStories = stories[username];
          const firstStory = userStories[0];

          return (
            <div
              key={username}
              className="flex flex-col items-center cursor-pointer"
              onClick={() => openUserStories(username)}
            >
              <div className="w-20 h-20 rounded-full border-2 border-pink-500 p-1">
                {firstStory.profileImage ? (
                  <img
                    src={`data:image/jpeg;base64,${firstStory.profileImage}`}
                    alt={username}
                    className="w-full h-full rounded-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full rounded-full bg-gray-400 flex items-center justify-center text-white font-bold text-lg">
                    {username.charAt(0).toUpperCase()}
                  </div>
                )}
              </div>
              <span className="text-xs mt-1">{username}</span>
            </div>
          );
        })}
      </div>

      {/* Modal */}
      {selectedUser && stories[selectedUser][currentIndex] && (
        <div className="fixed inset-0 bg-black z-50 flex justify-center items-center">
          <div className="relative w-full h-full flex items-center justify-center">
            <div className="relative bg-black w-full max-w-[420px] h-full flex flex-col">
              {/* Close Button */}
              <button
                className="absolute top-4 right-4 text-white z-50"
                onClick={closeModal}
              >
                <X size={28} />
              </button>

              <div className="flex-1 flex items-center justify-center relative">
                {/* Profile info */}
                <div className="absolute top-4 left-4 flex items-center space-x-2 text-white z-10">
                  <img
                    src={
                      stories[selectedUser][currentIndex].userProfileImage ||
                      "/default-avatar.png"
                    }
                    alt="profile"
                    className="w-8 h-8 rounded-full border"
                  />
                  <div>
                    <div className="flex items-center space-x-2">
                      <p className="font-semibold text-sm">
                        {stories[selectedUser][currentIndex].username}
                      </p>
                      <p className="text-xs opacity-80">●</p>
                      <p className="text-xs opacity-80">
                        {stories[selectedUser][currentIndex].audioName || ""}
                      </p>
                    </div>
                    <p className="text-xs opacity-80">
                      {moment(stories[selectedUser][currentIndex].createdAt).fromNow()}
                    </p>
                  </div>
                </div>

                {/* Media */}
                {stories[selectedUser][currentIndex].mediaType.startsWith("image") ? (
                  <img
                    src={`data:${stories[selectedUser][currentIndex].mediaType};base64,${stories[selectedUser][currentIndex].mediaData}`}
                    alt="story"
                    className="w-full h-full object-contain bg-black"
                  />
                ) : (
                  <video
                    src={`data:${stories[selectedUser][currentIndex].mediaType};base64,${stories[selectedUser][currentIndex].mediaData}`}
                    controls
                    autoPlay
                    muted={muted}
                    className="w-full h-full object-contain bg-black"
                  />
                )}

                {/* Audio */}
                {stories[selectedUser][currentIndex].audioData && (
                  <div className="absolute top-4 right-14 -translate-x-1/2 z-50">
                    <AudioPlayer
                      audioUrl={`data:${stories[selectedUser][currentIndex].audioType};base64,${stories[selectedUser][currentIndex].audioData}`}
                      isActive={!!selectedUser}
                      muted={muted}
                      setMuted={setMuted}
                    />
                  </div>
                )}

                {/* Caption */}
                {stories[selectedUser][currentIndex].caption && (
                  <p className="absolute bottom-20 left-0 right-0 px-4 text-white text-sm text-center">
                    {stories[selectedUser][currentIndex].caption}
                  </p>
                )}

                {/* Likes & Replies & Views */}
                <div className="absolute bottom-0 left-0 right-0 flex justify-between items-center p-4 bg-black bg-opacity-60">
                  <StoryLikes storyId={stories[selectedUser][currentIndex].id} />
                  <StoryReplies storyId={stories[selectedUser][currentIndex].id} />
                  <StoryViews storyId={stories[selectedUser][currentIndex].id} />
                </div>
              </div>
            </div>

            {/* Navigation Arrows */}
            {currentIndex > 0 && (
              <button
                onClick={prevStory}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-white bg-black bg-opacity-40 rounded-full p-2"
              >
                <ChevronLeft size={28} />
              </button>
            )}
            {currentIndex < stories[selectedUser].length - 1 && (
              <button
                onClick={nextStory}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-white bg-black bg-opacity-40 rounded-full p-2"
              >
                <ChevronRight size={28} />
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default FollowingStories;
