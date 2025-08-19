import React, { createContext, useState, useEffect } from "react";
import { getCurrentUser } from "../api/UserApi";

export const UserContext = createContext({ user: null, setUser: () => {} });

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const fetchUser = async () => {
            const storedUsername = localStorage.getItem("username");

            if (storedUsername) {
                console.log("Loaded Username from localStorage:", storedUsername);
                setUser({ username: storedUsername });
            } else {
                const userData = await getCurrentUser();
                if (userData) {
                    setUser(userData);
                    localStorage.setItem("username", userData.username);
                    console.log("Fetched and Stored Username:", userData.username);
                }
            }
        };

        fetchUser();
    }, []);

    return (
        <UserContext.Provider value={{ user, setUser }}>
            {children}
        </UserContext.Provider>
    );
};
