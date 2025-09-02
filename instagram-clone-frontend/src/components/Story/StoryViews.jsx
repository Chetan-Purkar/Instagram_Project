import { useEffect, useState } from "react";
import { addStoryView, getStoryViewCount } from "../../api/StoryApi";

const StoryViews = ({ storyId }) => {
  const [viewCount, setViewCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const recordView = async () => {
      if (!token) return;

      try {
        // Record that the user viewed this story
        await addStoryView(storyId, token);

        // Fetch updated view count
        const count = await getStoryViewCount(storyId, token);
        setViewCount(count);
      } catch (err) {
        console.error("Error recording story view:", err);
      } finally {
        setLoading(false);
      }
    };

    recordView();
    // Only run when storyId changes
  }, [storyId, token]);

  if (loading) {
    return (
      <div className="text-sm text-gray-400 mt-2 flex flex-col items-center">
        <span>👁️</span> Loading...
      </div>
    );
  }

  return (
    <div className="text-sm text-gray-400 mt-2 flex flex-col items-center">
      <span>👁️</span>
      {viewCount} {viewCount === 1 ? "view" : "views"}
    </div>
  );
};

export default StoryViews;
  