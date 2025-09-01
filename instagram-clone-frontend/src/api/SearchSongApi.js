import axios from "axios";

const BASE_URL = "http://localhost:8080/api/music";

export const searchSongs = async (query, token) => {
  try {
    const res = await axios.get(`${BASE_URL}/search`, {
      params: { query },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    console.log("Search results:", JSON.stringify(res.data, null, 2));
    return res.data;
  } catch (error) {
    console.error("Error searching songs:", error.response?.data || error.message, error);
    throw new Error(error.response?.data || error.message);
  }
};

export const fetchSongDetails = async (songId, token) => {
  try {
    const res = await axios.get(`${BASE_URL}/songs`, {
      params: { id: songId },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    console.log("Song details:", JSON.stringify(res.data, null, 2));
    return res.data;
  } catch (error) {
    console.error("Error fetching song details:", error.response?.data || error.message, error);
    throw new Error(error.response?.data || error.message);
  }
};

export const streamSong = async (songUrl, token) => {
  try {
    const encodedUrl = encodeURIComponent(songUrl);
    console.log("Streaming request URL:", `${BASE_URL}/stream?url=${encodedUrl}`);

    const res = await axios.get(`${BASE_URL}/stream`, {
      params: { url: encodedUrl },
      headers: {
        Authorization: `Bearer ${token}`,
      },
      responseType: "arraybuffer",
    });

    // Check if backend returned JSON error instead of audio
    if (res.headers['content-type']?.includes("application/json")) {
      const text = new TextDecoder().decode(res.data);
      throw new Error(JSON.parse(text).error || "Streaming error");
    }

    const contentType = res.headers['content-type'] 
      || (songUrl.endsWith(".mp4") || songUrl.endsWith(".m4a") ? "audio/mp4" : "audio/mpeg");

    console.log("Response Content-Type:", contentType);

    const audioBlob = new Blob([res.data], { type: contentType });
    const audioUrl = URL.createObjectURL(audioBlob);
    console.log("🎶 Playable Audio URL:", audioUrl);

    return audioUrl;
  } catch (error) {
    console.error("Error streaming song:", error.response?.data || error.message, error);
    throw new Error(error.response?.data || error.message);
  }
};
