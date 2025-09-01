import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/users";

export const getCurrentUser = async () => {
    try {
        const token = localStorage.getItem("token");

        if (!token) {
            console.warn("No authentication token found.");
            return null;
        }

        const response = await axios.get(`${API_BASE_URL}/current-user`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            withCredentials: true,
        });

        return response.data;
    } catch (error) {
        console.error("Error fetching current user:", error.response?.data || error.message);
        return null;
    }
};


// Fetch by username
export const getUserByUsername = async (username) => {
    try {
        const token = localStorage.getItem("token");

        if (!token) {
            console.warn("⚠️ No authentication token found.");
            return null;
        }

        const response = await axios.get(`${API_BASE_URL}/${username}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            withCredentials: true,
        });

        return response.data;
    } catch (error) {
        console.error(`❌ Error fetching user ${username}:`, error.response?.data || error.message);
        return null;
    }
};

// Fetch all users
export const getAllUsers = async () => {
    try {
        const token = localStorage.getItem("token"); // Ensure token is stored after login
        const response = await axios.get(`${API_BASE_URL}/all`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            withCredentials: true,  // Ensure cookies are sent if required
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching all users:", error);
        throw error;
    }
};

// Fetch all usernames
export const fetchAllUsernames = async () => {
    try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`${API_BASE_URL}/allusers`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            withCredentials: true,
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching usernames:", error);
        throw error;
    }
};


// ✅ Update User API
export const updateUser = async (userId, name, email, bio, profileImage) => {
    try {
        const token = localStorage.getItem("token");
        const formData = new FormData();
        formData.append("name", name);
        formData.append("email", email);
        formData.append("bio", bio);
        if (profileImage) {
            formData.append("profileImage", profileImage);
        }

        const response = await axios.put(`${API_BASE_URL}/${userId}/update`, formData, {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "multipart/form-data",
            },
            withCredentials: true,
        });

        return response.data;
    } catch (error) {
        console.error("Error updating user:", error.response?.data || error.message);
        throw error;
    }
};

// Update account privacy with JWT token
export const updatePrivacy = async (privacy, token) => {
  try {
    const response = await axios.put(`${API_BASE_URL}/privacy`,
      null,
      {
        params: { privacy }, // ?privacy=PUBLIC or ?privacy=PRIVATE
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error updating privacy:", error);
    throw error;
  }
};

// 🔍 Search users by username (partial match, first/middle letters)
export const searchUsers = async (username) => {
    try {
      const token = localStorage.getItem("token");
  
      const response = await axios.get(`${API_BASE_URL}/search`, {
        params: { username },
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      });
  
      return response.data;
    } catch (error) {
      console.error('Error searching users:', error);
      throw error;
    }
  };