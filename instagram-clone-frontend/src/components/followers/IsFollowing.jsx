import { useState, useEffect } from 'react';
import SearchFollowers from '../SearchFollowers';
import SearchFollowing from '../SearchFollowing';
import { getFollowers, getFollowing } from '../../api/FollowersApi';

const FollowStats = ({ username }) => {
  const [followersCount, setFollowersCount] = useState(0);
  const [followingCount, setFollowingCount] = useState(0);
  const [view, setView] = useState(null); // 'followers' or 'following'
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const [followersData, followingData] = await Promise.all([
          getFollowers(username),
          getFollowing(username),
        ]);
        setFollowersCount(followersData.length);
        setFollowingCount(followingData.length);
      } catch (err) {
        console.error('Error fetching follow stats:', err);
      }
    };

    fetchCounts();
  }, [username]);

  const closePopup = () => {
    setView(null);
    setSearchTerm('');
  };

  return (
    <div className="relative">
      <div className="flex gap-4 mb-4">
        <span
          className="cursor-pointer hover:text-blue-500"
          onClick={() => setView('following')}
        >
          <strong>{followingCount}</strong> Following
        </span>
        <span
          className="cursor-pointer hover:text-blue-500"
          onClick={() => setView('followers')}
        >
          <strong>{followersCount}</strong> Followers
        </span>
      </div>

      {view && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center">
          <div className="bg-gray-800 w-[90%] max-w-md rounded-xl shadow-lg p-6 relative">
            <button
              onClick={closePopup}
              className="absolute top-2 right-3 text-gray-300 hover:text-white text-xl font-bold"
            >
              &times;
            </button>
            <h2 className="text-lg font-semibold mb-4 capitalize text-white">{view}</h2>

            <input
              type="text"
              placeholder={`Search ${view}`}
              className="w-full mb-4 px-3 py-2 rounded-md border border-gray-600 bg-gray-700 text-white"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />

            <div className="max-h-[400px] overflow-y-auto">
              {view === 'followers' && (
                <SearchFollowers username={username} searchTerm={searchTerm} closePopup={closePopup}/>
              )}
              {view === 'following' && (
                <SearchFollowing username={username} searchTerm={searchTerm} closePopup={closePopup}/>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FollowStats;
