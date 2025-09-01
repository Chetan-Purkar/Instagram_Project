import { useEffect, useState } from "react";
import { getFollowers } from "../api/FollowersApi";
import { Link } from "react-router-dom";

const SearchFollowers = ({ username, searchTerm, closePopup }) => {
  const [followers, setFollowers] = useState([]);
  const [filtered, setFiltered] = useState([]);

  useEffect(() => {
    const fetchFollowers = async () => {
      try {
        const data = await getFollowers(username);
        setFollowers(data);
      } catch (error) {
        console.error("Error fetching followers:", error);
      }
    };

    fetchFollowers();
  }, [username]);

  useEffect(() => {
    const filteredData = followers.filter((user) =>
      user.followerUsername.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFiltered(filteredData);
  }, [searchTerm, followers]);

  return (
   <ul>
    {filtered.map((user) => (
      <li key={user.followerUsername} className="flex items-center gap-3 mb-3">
        <Link to={`/${user.followerUsername}`}>
          <img
            src={`data:image/jpeg;base64,${user.profileImage}`}
            alt={user.followerUsername}
            className="w-10 h-10 rounded-full object-cover cursor-pointer"
            onClick={closePopup} 
          />
        </Link>
        <Link
          to={`/${user.followerUsername}`}
          className="text-white hover:underline"
          onClick={closePopup} 
        >
          {user.followerUsername}
        </Link>
      </li>
    ))}
  </ul>

  );
};

export default SearchFollowers;
