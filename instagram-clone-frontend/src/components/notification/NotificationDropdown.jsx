import { useEffect, useState } from "react";
import { getNotifications, markAsRead } from "../../api/NotificationsApi";
import { acceptFollowRequest, rejectFollowRequest } from "../../api/FollowersApi";
import { subscribeNotifications, connectWebSocket, disconnectWebSocket } from "../../utils/websocket";

function NotificationsPage({ currentUser }) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [processing, setProcessing] = useState({});

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        setLoading(true);
        const data = await getNotifications();
        setNotifications(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error("Failed to fetch notifications:", err);
        setError("Oops! Could not load notifications.");
        setNotifications([]);
      } finally {
        setLoading(false);
      }
    };
    fetchNotifications();
  }, []);

  useEffect(() => {
    if (!currentUser?.id) return;
    connectWebSocket(currentUser.id);
    const unsubscribe = subscribeNotifications(currentUser.id, (notification) => {
      setNotifications((prev) => [notification, ...prev]);
    });
    return () => {
      disconnectWebSocket();
      if (unsubscribe) unsubscribe();
    };
  }, [currentUser?.id]);

  const handleMarkRead = async (id) => {
    try {
      await markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
    } catch (err) {
      console.error("Failed to mark notification as read:", err);
    }
  };

  const handleAction = async (followerId, action) => {
    setProcessing((prev) => ({ ...prev, [followerId]: true }));
    try {
      if (action === "accept") await acceptFollowRequest(followerId);
      else await rejectFollowRequest(followerId);

      setNotifications((prev) =>
        prev.map((n) =>
          n.relatedFollowerId === followerId
            ? { ...n, actionCompleted: true, relatedFollowerStatus: action.toUpperCase() }
            : n
        )
      );
    } catch (err) {
      console.error(`Failed to ${action} follow request:`, err);
    } finally {
      setProcessing((prev) => ({ ...prev, [followerId]: false }));
    }
  };

  const renderNotification = (n) => (
    <li
      key={n.id}
      className={`p-4 mb-3 rounded shadow-sm border transition-colors ${
        n.isRead ? "bg-gray-800" : "bg-gray-900 text-white border-blue-300"
      }`}
    >
      <div className="flex items-center gap-3">
        {n.sender?.profileImage && (
          <img
            src={`data:image/png;base64,${n.sender.profileImage}`}
            alt={n.sender.username}
            className="w-12 h-12 rounded-full border"
          />
        )}
        <div className="flex-1">
          <p className={`font-medium ${n.isRead ? "text-gray-300" : "text-gray-300"}`}>
            {n.content}
          </p>
          <small className="text-gray-400 text-sm">
            {n.createdAt
              ? new Intl.DateTimeFormat("en-US", {
                  dateStyle: "medium",
                  timeStyle: "short",
                }).format(new Date(n.createdAt))
              : ""}
          </small>
        </div>
        {!n.isRead && (
          <button
            onClick={() => handleMarkRead(n.id)}
            className="text-sm text-blue-500 hover:underline ml-2"
          >
            Mark Read
          </button>
        )}
      </div>

      {n.type === "FOLLOW_REQUEST" && !n.actionCompleted && (
        <div className="mt-3 flex gap-2">
          <button
            onClick={() => handleAction(n.relatedFollowerId, "accept")}
            disabled={processing[n.relatedFollowerId]}
            className="bg-green-500 hover:bg-green-600 disabled:opacity-50 text-white px-4 py-1 rounded"
          >
            {processing[n.relatedFollowerId] ? "..." : "Accept"}
          </button>
          <button
            onClick={() => handleAction(n.relatedFollowerId, "reject")}
            disabled={processing[n.relatedFollowerId]}
            className="bg-red-500 hover:bg-red-600 disabled:opacity-50 text-white px-4 py-1 rounded"
          >
            {processing[n.relatedFollowerId] ? "..." : "Reject"}
          </button>
        </div>
      )}
    </li>
  );

  return (
    <div className="max-w-xl mx-auto mt-10 p-4">
      <h2 className="text-2xl text-white font-bold mb-6">Notifications</h2>

      {loading && <p className="text-gray-500">Loading your notifications...</p>}
      {error && <p className="text-red-500">{error}</p>}
      {!loading && notifications.length === 0 && (
        <p className="text-gray-400 text-center mt-10">You have no notifications yet!</p>
      )}

      <ul>{notifications.map(renderNotification)}</ul>
    </div>
  );
}

export default NotificationsPage;
