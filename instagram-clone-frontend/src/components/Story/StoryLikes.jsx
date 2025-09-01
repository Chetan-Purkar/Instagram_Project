import { useEffect, useState } from "react";
import { likeStory, unlikeStory, getStoryLikeCount, getStoryLikes } from "../../api/StoryApi";

const StoryLikes = ({ storyId }) => {
  const [likeCount, setLikeCount] = useState(0);
  const [liked, setLiked] = useState(false);
  const token = localStorage.getItem("token");
  const currentUserId = parseInt(localStorage.getItem("userId")); // current user

  useEffect(() => {
    const fetchLikes = async () => {
      try {
        const count = await getStoryLikeCount(storyId, token);
        setLikeCount(count);

        const likes = await getStoryLikes(storyId, token);
        const userLiked = likes.some(like => like.userId === currentUserId);
        setLiked(userLiked);
      } catch (err) {
        console.error("Error fetching like info:", err);
      }
    };
    fetchLikes();
  }, [storyId, token, currentUserId]);

  const handleLike = async () => {
    try {
      if (!liked) {
        await likeStory(storyId, token);
        setLiked(true);
        setLikeCount(prev => prev + 1);
      } else {
        await unlikeStory(storyId, token);
        setLiked(false);
        setLikeCount(prev => Math.max(prev - 1, 0));
      }
    } catch (err) {
      console.error("Error updating like:", err);
    }
  };

  return (
    <div className="flex items-center gap-2 mt-2 flex flex-col">
      <button
        onClick={handleLike}
        className={` m-0 p-0 ${
          liked ? "text-white" : "text-white"
        }`}
      >
        {liked ? "❤️" : "🤍"}
      </button>
      <span className="text-gray-400 text-sm">{likeCount} {likeCount === 1 ? "like" : "likes"}</span>
    </div>
  );
};

export default StoryLikes;
