import React, { useState, useEffect } from "react";
import { getCurrentUser, updateUser } from "../api/UserApi";

const UpdateProfile = () => {
    const [userId, setUserId] = useState(null);
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [bio, setBio] = useState("");
    const [profileImage, setProfileImage] = useState(null);
    const [profileImageUrl, setProfileImageUrl] = useState("");
    const [message, setMessage] = useState("");

    useEffect(() => {
        const fetchUser = async () => {
            const user = await getCurrentUser();
            if (user) {
                setUserId(user.id);
                setName(user.name || "");
                setEmail(user.email || "");
                setBio(user.bio || "");
                setProfileImageUrl(user.profileImageUrl || "");
            }
        };
        fetchUser();
    }, []);

    const handleFileChange = (e) => {
        setProfileImage(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");

        try {
            const response = await updateUser(userId, name, email, bio, profileImage);
            setMessage("Profile updated successfully!");
            setProfileImageUrl(response.updated.profileImageUrl || profileImageUrl);
        } catch (error) {
            setMessage("Error updating profile. Please try again.");
        }
    };

    return (
        <div className="max-w-lg mx-auto mt-10 p-6 bg-white shadow-md rounded-lg">
            <h2 className="text-2xl font-bold mb-4 text-center">Update Profile</h2>

            {message && (
                <p className={`text-center ${message.includes("success") ? "text-green-500" : "text-red-500"}`}>
                    {message}
                </p>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
                {/* Display Profile Image */}
                {profileImageUrl && (
                    <div className="text-center">
                        <img src={`http://localhost:8080/${profileImageUrl}`} alt="Profile" className="w-24 h-24 rounded-full mx-auto" />
                    </div>
                )}

                {/* Name */}
                <div>
                    <label className="block text-gray-700 font-semibold">Name:</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                        required
                    />
                </div>

                {/* Email */}
                <div>
                    <label className="block text-gray-700 font-semibold">Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                        required
                    />
                </div>

                {/* Bio */}
                <div>
                    <label className="block text-gray-700 font-semibold">Bio:</label>
                    <textarea
                        value={bio}
                        onChange={(e) => setBio(e.target.value)}
                        className="w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                        rows="3"
                    />
                </div>

                {/* Profile Image Upload */}
                <div>
                    <label className="block text-gray-700 font-semibold">Profile Image:</label>
                    <input type="file" onChange={handleFileChange} className="w-full p-2 border rounded-md" />
                </div>

                {/* Submit Button */}
                <button
                    type="submit"
                    className="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition duration-300"
                >
                    Update Profile
                </button>
            </form>
        </div>
    );
};

export default UpdateProfile;
