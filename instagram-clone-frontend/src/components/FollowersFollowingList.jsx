import React, { useEffect, useState } from 'react';
import { getFollowers, getFollowing } from '../api/FollowersApi'; // adjust path as needed

const FollowersFollowingList = ({ username }) => {
  const [followers, setFollowers] = useState([]);
  const [following, setFollowing] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [followersData, followingData] = await Promise.all([
          getFollowers(username),
          getFollowing(username),
        ]);

        setFollowers(followersData);
        setFollowing(followingData);
        console.log('Followers:', followersData);
        console.log('Following:', followingData);
        
      } catch (error) {
        console.error('Error fetching followers/following:', error);
      } finally {
        setLoading(false);
      }
    };

    if (username) {
      fetchData();
    }
  }, [username]);

  if (loading) {
    return <div>Loading followers and following...</div>;
  }

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-2">Followers</h2>
      <ul className="mb-4 list-disc pl-5">
        {followers.length > 0 ? (
          followers.map((user, idx) => (
            <div key={idx}>
              <div className="flex items-center mb-2">
                <img
                   src={`data:image/jpeg;base64,${user.profileImage}`}
                  alt={`${user.username}'s profile`}
                  className="w-6 h-6 rounded-full mr-2"
                  />
                  {user.username} {user.isFollowing ? '(You are following)' : ''}
                  <p key={idx}>{user.followerUsername}</p>
              </div>
            </div>
          ))
        ) : (
          <li>No followers found.</li>
        )}
      </ul>

      <h2 className="text-xl font-bold mb-2">Following</h2>
      <ul className="list-disc pl-5">
        {following.length > 0 ? (
          following.map((user, idx) => (
            <p key={idx}>{user.followingUsername}</p>
          ))
        ) : (
          <li>Not following anyone.</li>
        )}
      </ul>
    </div>
  );
};

export default FollowersFollowingList;
