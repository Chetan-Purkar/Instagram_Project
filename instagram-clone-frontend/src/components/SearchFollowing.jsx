import { useEffect, useState } from "react";
import { getFollowing } from "../api/FollowersApi";
import { Link } from "react-router-dom";

const SearchFollowing = ({ username, searchTerm, closePopup }) => {
  const [following, setFollowing] = useState([]);
  const [filtered, setFiltered] = useState([]);

  useEffect(() => {
    const fetchFollowing = async () => {
      try {
        const data = await getFollowing(username);
        setFollowing(data);
      } catch (error) {
        console.error("Error fetching following:", error);
      }
    };

    fetchFollowing();
  }, [username]);

  useEffect(() => {
    const filteredData = following.filter((user) =>
      user.followingUsername.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFiltered(filteredData);
  }, [searchTerm, following]);

  return (
    <ul>
      {filtered.map((user) => (
        <li key={user.followingUsername} className="flex items-center gap-3 mb-3">
          <Link to={`/${user.followingUsername}`}>
            <img
              src={`data:image/jpeg;base64,${user.profileImage}`}
              alt={user.followingUsername}
              className="w-10 h-10 rounded-full object-cover cursor-pointer"
              onClick={closePopup} 
            />
          </Link>
          <Link
            to={`/${user.followingUsername}`}
            className="text-white hover:underline"
            onClick={closePopup} 
          >
            {user.followingUsername}
          </Link>
        </li>
      ))}
    </ul>

  );
};

export default SearchFollowing;
