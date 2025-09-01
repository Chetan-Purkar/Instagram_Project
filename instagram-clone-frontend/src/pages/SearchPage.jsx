import { useState } from "react";
import SearchUser from "../components/UserSearchBar"; // adjust path as needed

const SearchPage = () => {
    const [searchQuery, setSearchQuery] = useState("");

    return (
        <div className="max-w-2xl mx-auto mt-10 p-4">
            <input
                type="text"
                placeholder="Search by username..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded mb-4"
            />
            <SearchUser query={searchQuery} />
        </div>
    );
};

export default SearchPage;
