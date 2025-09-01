import React, { useEffect, useState } from 'react';
import { getFollowing } from '../../api/FollowersApi';

const FollowingList = ({ username }) => {
  const [following, setFollowing] = useState([]);

  useEffect(() => {
    const fetchFollowing = async () => {
      try {
        const data = await getFollowing(username);
        setFollowing(data);
      } catch (err) {
        console.error('Error fetching following:', err);
      }
    };

    fetchFollowing();
  }, [username]);

  return (
    <div>
      {following.length === 0 ? (
        <p>Not following anyone.</p>
      ) : (
        <ul>
          {following.map((user, idx) => (
            <li key={idx} className="flex items-center mb-2">
              <img
                src={`data:image/jpeg;base64,${user.profileImage}`}
                alt="profile"
                className="w-6 h-6 rounded-full mr-2"
              />
              <span>{user.followingUsername}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default FollowingList;
