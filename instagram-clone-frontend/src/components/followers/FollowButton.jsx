import { useEffect, useState, useCallback } from "react";
import { getFollowStatus, toggleFollow } from "../../api/FollowersApi";

const FollowButton = ({ currentUsername, targetUsername }) => {
  const [status, setStatus] = useState("FOLLOW");
  const [loading, setLoading] = useState(true);
  const [showConfirm, setShowConfirm] = useState(false);

  // ✅ useCallback so dependency is stable
  const fetchStatus = useCallback(async () => {
    try {
      const data = await getFollowStatus(currentUsername, targetUsername);
      setStatus(data.status);
    } catch (error) {
      console.error("Error fetching follow status:", error);
    } finally {
      setLoading(false);
    }
  }, [currentUsername, targetUsername]);

  useEffect(() => {
    fetchStatus();
  }, [fetchStatus]);

  const handleToggle = async () => {
    try {
      await toggleFollow(currentUsername, targetUsername);

      // Optimistic update
      if (status === "FOLLOW") setStatus("FOLLOWING");
      else setStatus("FOLLOW");

      // ✅ sync with backend
      fetchStatus();
    } catch (error) {
      console.error("Error toggling follow:", error);
    } finally {
      setShowConfirm(false);
    }
  };

  return (
    <div className="relative inline-block">
      {/* FOLLOW */}
      {status === "FOLLOW" && (
        <button
          onClick={handleToggle}
          disabled={loading}
          className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Follow
        </button>
      )}

      {/* REQUESTED */}
      {status === "PENDING" && (
        <button
          onClick={() => setShowConfirm(true)}
          disabled={loading}
          className="px-2 py-1 bg-gray-400 text-white rounded hover:bg-gray-500"
        >
          Requested
        </button>
      )}

      {/* FOLLOWING */}
      {status === "FOLLOWING" && (
        <button
          onClick={() => setShowConfirm(true)}
          disabled={loading}
          className="px-2 py-1 bg-blue-500 text-white rounded"
        >
          Following
        </button>
      )}

      {/* CONFIRMATION POPUP */}
      {showConfirm && (
        <div className="absolute mt-2 w-56 bg-gray-700 border rounded shadow-lg p-2 z-10">
          <p className="text-gray-100 mb-3">
            {status === "FOLLOWING"
              ? `Do you really want to unfollow ${targetUsername}?`
              : `Cancel follow request to ${targetUsername}?`}
          </p>
          <div className="flex justify-end gap-2">
            <button
              onClick={handleToggle}
              className="px-1 py-1 rounded bg-red-500 text-white hover:bg-red-600"
            >
              Unfollow
            </button>
            <button
              onClick={() => setShowConfirm(false)}
              className="px-1 py-1 rounded bg-gray-600 hover:bg-gray-500"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default FollowButton;
