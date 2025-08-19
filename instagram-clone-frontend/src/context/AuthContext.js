// context/AuthContext.js
import { createContext, useState, useEffect } from 'react';
import { AuthService } from '../services/AuthService';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [auth, setAuth] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            setAuth({ token });
        }
    }, []);

    const login = async (authData) => {
        const data = await AuthService.login(authData);
        setAuth(data);
    };

    const signup = async (userData) => {
        const data = await AuthService.signup(userData);
        if (data) {
            const { token } = data;
            const username = userData.username;
            localStorage.setItem("token", token);
            localStorage.setItem("username", username);
            setAuth({ token, username });
        }
    };

    const logout = () => {
        AuthService.logout(); // optional: clear session/cookie on backend
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        setAuth(null);
    };

    return (
        <AuthContext.Provider value={{ auth, login, logout, signup }}>
            {children}
        </AuthContext.Provider>
    );
};