import { useEffect, useState } from "react";
import { addStoryView, getStoryViewCount } from "../../api/StoryApi";

const StoryViews = ({ storyId }) => {
  const [viewCount, setViewCount] = useState(0);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const recordView = async () => {
      try {
        if (!token) return;
        await addStoryView(storyId, token); // record that user viewed this story
        const count = await getStoryViewCount(storyId, token); // get updated count
        setViewCount(count);
      } catch (err) {
        console.error("Error recording story view:", err);
      }
    };

    recordView();
  }, [storyId, token]);

  return (
    <div className="text-sm text-gray-400 mt-2 flex flex-col items-center">
      <span>👁️</span>
      {viewCount} {viewCount === 1 ? "view" : "views"}
    </div>
  );
};
    
export default StoryViews;
                