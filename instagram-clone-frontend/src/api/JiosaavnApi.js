import axios from "axios";

// 🔍 Search songs
export const searchSongs = async (query, page = 1) => {
  try {
    const res = await axios.get(`https://saavn.dev/api/search/songs`, {
      params: { query, page },
    });
    return res.data; // ✅ return only data
  } catch (error) {
    console.error("Error in searchSongs API:", error);
    throw error;
  }
};

