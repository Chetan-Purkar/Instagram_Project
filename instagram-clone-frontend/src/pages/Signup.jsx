// Signup.jsx
import React, { useState } from "react";
import { AuthService } from "../services/AuthService";
import { useNavigate } from "react-router-dom";

const Signup = () => {
    const [userData, setUserData] = useState({ username: "", email: "", password: "" });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setUserData({ ...userData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await AuthService.signup(userData);
            if (response) {
                alert("Signup successful! Please login.");
                navigate("/login");
            } else {
                setError("Signup failed. Try again.");
            }
        } catch (err) {
            setError("Error signing up. Please check your details.");
        }
        setLoading(false);
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-sm">
                <h2 className="text-2xl font-bold text-center mb-4">Sign Up</h2>
                {error && <p className="text-red-500 text-sm text-center">{error}</p>}

                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <input
                        type="text"
                        name="username"
                        placeholder="Username"
                        value={userData.username}
                        onChange={handleChange}
                        className="p-3 border rounded-md focus:ring-2 focus:ring-blue-400 outline-none"
                        required
                    />
                    <input
                        type="email"
                        name="email"
                        placeholder="Email"
                        value={userData.email}
                        onChange={handleChange}
                        className="p-3 border rounded-md focus:ring-2 focus:ring-blue-400 outline-none"
                        required
                    />
                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={userData.password}
                        onChange={handleChange}
                        className="p-3 border rounded-md focus:ring-2 focus:ring-blue-400 outline-none"
                        required
                    />
                    <button
                        type="submit"
                        className={`bg-blue-500 text-white p-3 rounded-md ${
                            loading ? "opacity-50 cursor-not-allowed" : "hover:bg-blue-600"
                        }`}
                        disabled={loading}
                    >
                        {loading ? "Signing up..." : "Sign Up"}
                    </button>
                </form>

                <p className="text-center text-sm mt-4">
                    Already have an account? 
                    <a href="/login" className="text-blue-500 hover:underline"> Login</a>
                </p>
            </div>
        </div>
    );
};

export default Signup;
