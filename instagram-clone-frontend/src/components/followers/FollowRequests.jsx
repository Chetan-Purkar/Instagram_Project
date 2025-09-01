import { useEffect, useState } from "react";
import {
  getPendingRequests,
  acceptFollowRequest,
  rejectFollowRequest,
} from "../../api/FollowersApi";

const FollowRequests = ({ username }) => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  // ✅ Load pending requests
  useEffect(() => {
    const fetchRequests = async () => {
      try {
        const data = await getPendingRequests(username);
        setRequests(data || []); // ensure safe array
      } catch (error) {
        console.error("Error fetching follow requests:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchRequests();
  }, [username]);

  // ✅ Accept a request
  const handleAccept = async (followerUsername) => {
    try {
      const res = await acceptFollowRequest(followerUsername, username);
      if (res.status === "ACCEPTED") {
        setRequests((prev) =>
          prev.filter((req) => req.username !== followerUsername)
        );
      }
    } catch (error) {
      console.error("Error accepting request:", error);
    }
  };

  // ❌ Reject a request
  const handleReject = async (followerUsername) => {
    try {
      const res = await rejectFollowRequest(followerUsername, username);
      if (res.status === "REJECTED" || res.success) {
        setRequests((prev) =>
          prev.filter((req) => req.username !== followerUsername)
        );
      }
    } catch (error) {
      console.error("Error rejecting request:", error);
    }
  };  

  if (loading) return <p className="text-gray-500">Loading requests...</p>;
  if (requests.length === 0)
    return <p className="text-gray-500">No pending requests</p>;

  return (
    <div className="p-4 rounded-2xl shadow-md">
      <h2 className="text-lg font-semibold mb-4">Follow Requests</h2>
      <ul className="space-y-3">
        {requests.map((req) => (
          <li
            key={req.id}
            className="flex justify-between items-center border p-3 rounded-lg"
          >
            {/* Profile Info */}
            <div className="flex items-center gap-3">
              <img
                src={req.profileImage || "/default-avatar.png"}
                alt={req.username}
                className="w-10 h-10 rounded-full object-cover"
              />
              <span className="font-medium">{req.username}</span>
            </div>

            {/* Actions */}
            <div className="space-x-2">
              <button
                onClick={() => handleAccept(req.username)}
                className="px-3 py-1 bg-green-500 text-white rounded-lg hover:bg-green-600 transition"
              >
                Accept
              </button>
              <button
                onClick={() => handleReject(req.username)}
                className="px-3 py-1 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
              >
                Reject
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default FollowRequests;
