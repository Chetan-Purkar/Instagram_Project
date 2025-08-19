import React, { useEffect, useState } from 'react';
import { getFollowers } from '../api/FollowersApi';

const FollowersList = ({ username }) => {
  const [followers, setFollowers] = useState([]);

  useEffect(() => {
    const fetchFollowers = async () => {
      try {
        const data = await getFollowers(username);
        console.log(data);
        
        setFollowers(data);
      } catch (err) {
        console.error('Error fetching followers:', err);
      }
    };

    fetchFollowers();
  }, [username]);

  return (
    <div>
      {followers.length === 0 ? (
        <p>No followers yet.</p>
      ) : (
        <ul>
          {followers.map((user, idx) => (
            <li key={idx} className="flex items-center mb-2">
              <img
                src={`data:image/jpeg;base64,${user.profileImage}`}
                alt="profile"
                className="w-6 h-6 rounded-full mr-2"
              />
              <span>{user.followerUsername}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default FollowersList;
