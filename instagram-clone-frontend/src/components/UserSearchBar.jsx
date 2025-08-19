import React, { useState, useEffect } from "react";
import { searchUsers } from "../api/UserApi"; // adjust path as needed

const SearchUser = ({ query }) => {
    const [users, setUsers] = useState([]);  // Default to empty array
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!query) {
            setUsers([]);
            return;
        }
    
        const fetchUsers = async () => {
            try {
                setLoading(true);
                const result = await searchUsers(query);    
    
                if (Array.isArray(result)) {
                    setUsers(result); // ✅ Correct format
                    console.log("Fetched users:", result);
                    
                } else {
                    console.warn("Unexpected format, expected an array:", result);
                    setUsers([]);
                }
            } catch (error) {
                console.error("Error fetching users:", error);
                setUsers([]);
            } finally {
                setLoading(false);
            }
        };
    
        fetchUsers();
    }, [query]);
    

    return (
        <div className="p-4">
            {loading && <p className="text-gray-500">Searching...</p>}
            {!loading && users.length === 0 && query && <p>No users found.</p>}  {/* Check if users array has data */}
            <ul className="space-y-4">
            {users.length > 0 ? (
                users.map((user) => (
                    <li key={user.id} className="flex items-center gap-4 border-b pb-2">
                        <img
                            src={`data:image/jpeg;base64,${user.profileImage}`}
                            alt={user.username}
                            className="w-10 h-10 rounded-full object-cover"
                        />
                        <div>
                            <p className="font-semibold text-gray-600">{user.username}</p>
                            <p className="text-sm text-gray-300">{user.name}</p>
                        </div>
                    </li>
                ))
            ) : (
                !loading && query && <p>No users found.</p>
            )}
        </ul>

        </div>
    );
};

export default SearchUser;
