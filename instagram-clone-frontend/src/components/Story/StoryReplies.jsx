// src/components/stories/StoryReplies.jsx
import { useEffect, useState } from "react";
import { addStoryReply, getStoryReplyCount } from "../../api/StoryApi";

const StoryReplies = ({ storyId, onReplyAdded }) => {
  const [replyCount, setReplyCount] = useState(0);
  const [newReply, setNewReply] = useState("");
  const token = localStorage.getItem("token"); // JWT token

  // Fetch reply count on mount or storyId change
  useEffect(() => {
    const fetchReplyCount = async () => {
      try {
        const count = await getStoryReplyCount(storyId, token);
        setReplyCount(count);
      } catch (err) {
        console.error("Error fetching reply count:", err);
      }
    };

    fetchReplyCount();
  }, [storyId, token]);

  // Handle adding a new reply
  const handleAddReply = async () => {
    if (!newReply.trim()) return;

    try {
      await addStoryReply(storyId, newReply, token);
      setReplyCount((prev) => prev + 1); // increment reply count
      setNewReply("");
      if (onReplyAdded) onReplyAdded(); // optional callback
    } catch (err) {
      console.error("Error adding reply:", err);
      alert("Failed to add reply");
    }
  };

  return (
    <div className="story-replies flex flex-col">
      <p className="text-sm font-semibold text-gray-300 mb-2">
        Replies ({replyCount})
      </p>

      {/* Input to add new reply */}
      <div className="flex gap-2">
        <input
          type="text"
          placeholder="Write a reply..."
          className="flex-grow border rounded p-2 bg-gray-900 text-white"
          value={newReply}
          onChange={(e) => setNewReply(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") handleAddReply();
          }}
        />
        <button
          onClick={handleAddReply}
          className="bg-blue-600 text-white px-4 py-2 rounded"
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default StoryReplies;
