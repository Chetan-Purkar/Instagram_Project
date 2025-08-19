// api/AuthApi.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export const signup = async (userData) => {
    return await axios.post(`${API_URL}/signup`, userData);
};
export const login = async (authRequest) => {
    try {
        const response = await axios.post(`${API_URL}/login`, authRequest, { withCredentials: true });
        if (response.status !== 200) {
            throw new Error("Login failed");
        }

        const { token, userId, username } = response.data;

        localStorage.setItem("token", token);
        localStorage.setItem("username", username);
        localStorage.setItem("userId", userId);

        return response.data;
    } catch (error) {
        console.error("Login error:", error);
        return null;
    }
};
