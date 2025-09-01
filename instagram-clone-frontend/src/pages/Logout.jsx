import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { logout } from "../api/AuthApi";

const LogoutButton = () => {
    const [showConfirm, setShowConfirm] = useState(false);
    const navigate = useNavigate();

    const handleLogout = async () => {
        const success = await logout();
        if (success) {
            navigate("/login");
        }
    };

    return (
        <>
            <button
                onClick={() => setShowConfirm(true)}
                className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-md"
            >
                Logout
            </button>

            {showConfirm && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
                    <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg w-80">
                        <h3 className="text-lg font-semibold mb-4 text-gray-900 dark:text-white">
                            Confirm Logout
                        </h3>
                        <p className="mb-6 text-gray-700 dark:text-gray-300">
                            Are you sure you want to logout?
                        </p>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setShowConfirm(false)}
                                className="px-4 py-2 bg-gray-300 hover:bg-gray-400 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-md"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleLogout}
                                className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-md"
                            >
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

export default LogoutButton;
