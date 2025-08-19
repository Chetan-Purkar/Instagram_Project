import React, { useEffect, useState } from "react";
import { toggleFollow, isFollowing } from "../api/FollowersApi";

const FollowButton = ({ currentUsername, targetUsername }) => {
    const [isFollowingStatus, setIsFollowing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [showConfirm, setShowConfirm] = useState(false);

    useEffect(() => {
        const checkFollowStatus = async () => {
            try {
                const status = await isFollowing(currentUsername, targetUsername);
                setIsFollowing(status);
            } catch (error) {
                console.error("Error checking follow status:", error);
            } finally {
                setLoading(false);
            }
        };
        checkFollowStatus();
    }, [currentUsername, targetUsername]);

    const handleFollow = async () => {
        try {
            await toggleFollow(currentUsername, targetUsername);
            setIsFollowing(true);
        } catch (error) {
            console.error("Failed to follow:", error);
        }
    };

    const handleUnfollowConfirm = async () => {
        try {
            await toggleFollow(currentUsername, targetUsername);
            setIsFollowing(false);
            setShowConfirm(false);
        } catch (error) {
            console.error("Failed to unfollow:", error);
        }
    };

    if (loading) return null;

    return (
        <div className="relative">
            {isFollowingStatus ? (
                <>
                    <button
                        onClick={() => setShowConfirm(true)}
                        className="px-3 py-1 rounded text-black border border-gray-400 bg-gray-300 hover:bg-gray-100"
                    >
                        following
                    </button>

                    {showConfirm && (
                        <div className="absolute top-14 left-0 w-72 bg-white border rounded shadow-lg p-4 z-10">
                            <p className="mb-4 text-gray-700">
                                Unfollow <strong>{targetUsername}</strong>?
                            </p>
                            <div className="flex justify-end space-x-2">
                                <button
                                    onClick={() => setShowConfirm(false)}
                                    className="px-3 py-1 bg-gray-300 text-gray-800 rounded"
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleUnfollowConfirm}
                                    className="px-2 py-1 rounded text-black border border-gray-400 bg-gray-300 hover:bg-gray-100"
                                    >
                                    Unfollow
                                </button>
                            </div>
                        </div>
                    )}
                </>
            ) : (
                <button
                    onClick={handleFollow}
                    className="px-4 py-1 rounded bg-blue-500 text-white"
                >
                    Follow
                </button>
            )}
        </div>
    );
};

export default FollowButton;
