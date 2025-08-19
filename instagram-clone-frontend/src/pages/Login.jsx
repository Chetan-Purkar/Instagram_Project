import React, { useState, useContext } from "react";
import { login } from "../api/AuthApi";
import { UserContext } from "../context/UserContext";
import { useNavigate } from "react-router-dom";

const Login = () => {
    const [authRequest, setAuthRequest] = useState({ username: "", password: "" });
    const { setUser } = useContext(UserContext);
    const navigate = useNavigate();
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setAuthRequest({ ...authRequest, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        
        const authResponse = await login(authRequest);

        if (authResponse) {
            setUser({ username: authRequest.username });
            navigate("/home");
        } else {
            setError("Invalid username or password.");
        }
        setLoading(false);
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-sm">
                <h2 className="text-2xl font-bold text-center mb-4">Login</h2>
                {error && <p className="text-red-500 text-sm text-center">{error}</p>}
                
                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <input
                        type="text"
                        name="username"
                        placeholder="Username"
                        value={authRequest.username}
                        onChange={handleChange}
                        className="p-3 border rounded-md focus:ring-2 focus:ring-blue-400 outline-none"
                        required
                    />
                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={authRequest.password}
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
                        {loading ? "Logging in..." : "Login"}
                    </button>
                </form>

                <p className="text-center text-sm mt-4">
                    Don't have an account? 
                    <a href="/signup" className="text-blue-500 hover:underline"> Sign up</a>
                </p>
            </div>
        </div>
    );
};

export default Login;
