// File: components/LikeButton.jsx
import React from "react";
import { toggleLike } from "../api/LikesApi";

const LikeButton = ({ postId, likeInfo, setLikesByPost }) => {
    const handleToggleLike = async () => {
        const current = likeInfo;
        
        const optimisticUpdate = {
            liked: !current?.liked,
            count: current?.liked ? current.count - 1 : current.count + 1,
        };

        // Optimistically update UI
        setLikesByPost((prev) => ({ ...prev, [postId]: optimisticUpdate }));

        try {
            const result = await toggleLike(postId);
            if (!result) throw new Error("Failed to toggle like");
        } catch (error) {
            console.error("Toggle like failed:", error);
            // Rollback UI on error
            setLikesByPost((prev) => ({ ...prev, [postId]: current }));
        }
    };

    return (
        <button onClick={handleToggleLike} className="flex items-center text-blue-500">
            <span className="text-lg">{likeInfo.liked ? "❤️" : "🤍"}</span>
            <span className="ml-1">{likeInfo.count} Likes</span>
        </button>
    );
};

export default LikeButton;
