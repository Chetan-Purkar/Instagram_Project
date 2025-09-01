import { useEffect, useState, useCallback } from "react";
import moment from "moment";
import { getMyStories } from "../../api/StoryApi";
import StoryLikes from "./StoryLikes";
import StoryReplies from "./StoryReplies";
import StoryViews from "./StoryViews"

import { X, ChevronLeft, ChevronRight } from "lucide-react";

const MyStories = () => {
  const [stories, setStories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewing, setViewing] = useState(false);
  const [currentIndex, setCurrentIndex] = useState(0);

  // ✅ Fetch my stories
  useEffect(() => {
    const fetchStories = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("token");
        const data = await getMyStories(token);
        setStories(data || []);
      } catch (err) {
        console.error("Failed to fetch my stories:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchStories();
  }, []);

  const closeModal = useCallback(() => {
    setViewing(false);
    setCurrentIndex(0);
  }, []);

  const nextStory = useCallback(() => {
    if (currentIndex < stories.length - 1) {
      setCurrentIndex((i) => i + 1);
    } else {
      closeModal();
    }
  }, [currentIndex, stories.length, closeModal]);

  const prevStory = useCallback(() => {
    if (currentIndex > 0) {
      setCurrentIndex((i) => i - 1);
    }
  }, [currentIndex]);

  // ✅ Keyboard navigation
  useEffect(() => {
    const handleKey = (e) => {
      if (e.key === "ArrowRight") nextStory();
      if (e.key === "ArrowLeft") prevStory();
      if (e.key === "Escape") closeModal();
    };
    window.addEventListener("keydown", handleKey);
    return () => window.removeEventListener("keydown", handleKey);
  }, [nextStory, prevStory, closeModal]);

  if (loading) return <p className="p-4">Loading your stories...</p>;

  return (
    <div className="">
      {/* ✅ My story avatar (like FollowingStories style) */}
      <div
          className="flex flex-col items-center cursor-pointer"
          onClick={() => {
            if (stories.length > 0) {
              setViewing(true);
              setCurrentIndex(0);
            }
          }}
        >
          <div className="w-20 h-20 rounded-full border-2 border-blue-500 p-1">
            {stories.length > 0 ? (
              stories[0].mediaType.startsWith("image") ? (
                <img
                  src={`data:${stories[0].mediaType};base64,${stories[0].mediaData}`}
                  alt="My story"
                  className="w-full h-full rounded-full object-cover"
                />
              ) : (
                <video
                  src={`data:${stories[0].mediaType};base64,${stories[0].mediaData}`}
                  className="w-full h-full rounded-full object-cover"
                />
              )
            ) : (
              <img
                src={localStorage.getItem("profileImage") || "/default-avatar.png"}
                alt="Profile"
                className="w-full h-full rounded-full object-cover"
              />
            )}
          </div>
          <span className="text-xs mt-1">Your Story</span>
        </div>


      {/* ✅ Fullscreen modal like FollowingStories */}
      {viewing && stories[currentIndex] && (
        <div className="fixed inset-0 bg-black z-50 flex justify-center items-center">
          <div className="relative w-full h-full flex items-center justify-center">
            {/* Frame */}
            <div className="relative bg-black w-full max-w-[420px] h-full flex flex-col">
              {/* Close button */}
              <button
                className="absolute top-4 right-4 text-white z-50"
                onClick={closeModal}
              >
                <X size={28} />
              </button>

              {/* Story content */}
              <div className="flex-1 flex items-center justify-center relative">
                <div className="absolute top-4 left-4 flex items-center space-x-2 text-white z-10">
                  <img
                    src={stories[currentIndex].userProfileImage || "/default-avatar.png"}
                    alt="profile"
                    className="w-8 h-8 rounded-full border"
                  />
                  <div>
                    <p className="font-semibold text-sm">
                      {stories[currentIndex].username}
                    </p>
                    <p className="text-xs opacity-80">
                      {moment(stories[currentIndex].createdAt).fromNow()}
                    </p>
                  </div>
                </div>

                {stories[currentIndex].mediaType.startsWith("image") ? (
                  <img
                    src={`data:${stories[currentIndex].mediaType};base64,${stories[currentIndex].mediaData}`}
                    alt="story"
                    className="w-full h-full object-contain bg-black"
                  />
                ) : (
                  <video
                    src={`data:${stories[currentIndex].mediaType};base64,${stories[currentIndex].mediaData}`}
                    autoPlay
                    className="w-full h-full object-contain bg-black"
                  />
                )}

                {/* Caption */}
                {stories[currentIndex].caption && (
                  <p className="absolute bottom-20 left-0 right-0 px-4 text-white text-sm text-center">
                    {stories[currentIndex].caption}
                  </p>
                )}

                {/* Likes & Replies */}
                <div className="absolute bottom-0 left-0 right-0 flex flex-row justify-between items-center p-4 bg-black bg-opacity-60">
                    <StoryLikes storyId={stories[currentIndex].id} />
                    <StoryReplies storyId={stories[currentIndex].id} />
                    <StoryViews storyId={stories[currentIndex].id} /> 
                </div>
              </div>
            </div>

            {/* Navigation arrows */}
            {currentIndex > 0 && (
              <button
                onClick={prevStory}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-white bg-black bg-opacity-40 rounded-full p-2"
              >
                <ChevronLeft size={28} />
              </button>
            )}
            {currentIndex < stories.length - 1 && (
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

export default MyStories;
