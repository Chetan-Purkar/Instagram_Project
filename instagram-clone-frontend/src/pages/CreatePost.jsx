import React, { useState } from "react";
import { createPost } from "../api/PostApi";
import SearchSong from "../components/song/SearchSong";

const CreatePost = () => {
  const [media, setMedia] = useState(null);
  const [audio, setAudio] = useState(null);
  const [audioName, setAudioName] = useState("");
  const [caption, setCaption] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleFileChange = (event) => {
    setMedia(event.target.files[0]);
    setError("");
    setSuccess("");
  };

  const handleAudioChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAudio(file);
      setAudioName(file.name); // store file name separately
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!media) {
      setError("⚠️ Please select an image or video.");
      return;
    }

    const formData = new FormData();
    formData.append("mediaData", media);
    formData.append("mediaType", media.type);
    formData.append("caption", caption);
    if (audio) {
      formData.append("audioData", audio);
      formData.append("audioName", audioName);
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const token = localStorage.getItem("token");
      await createPost(formData, token);
      setSuccess("✅ Post uploaded successfully!");
      setCaption("");
      setMedia(null);
      setAudio(null);
    } catch (err) {
      setError(err.message || "Error uploading post.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-8 p-6 bg-gray-900 rounded-2xl shadow-lg text-white">
      <h2 className="text-xl font-bold mb-4 text-center">📝 Create a Post</h2>

      {/* Alerts */}
      {error && <p className="text-red-400 mb-3 text-center">{error}</p>}
      {success && <p className="text-green-400 mb-3 text-center">{success}</p>}

      <form onSubmit={handleSubmit} onKeyDown={(e) => {
          if (e.key === "Enter") {
            e.preventDefault(); // stops Enter from submitting the form
          }
        }} className="space-y-5"
        >


        {/* Media Upload */}
        <div>
          <label className="block text-sm font-medium mb-2">Upload Image/Video</label>
          <input
            type="file"
            accept="image/*,video/*"
            onChange={handleFileChange}
            className="w-full text-sm text-gray-300 file:mr-3 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-600 file:text-white hover:file:bg-blue-500 cursor-pointer"
          />
          {media && (
            <p className="mt-2 text-xs text-gray-400">
              Selected: <span className="text-blue-400">{media.name}</span>
            </p>
          )}
        </div>

        {/* Audio Upload */}
        <div>
          <label className="block text-sm font-medium mb-2">Upload Audio (Optional)</label>
          <input
            type="file"
            accept="audio/*"
            onChange={handleAudioChange}
            className="w-full text-sm text-gray-300 file:mr-3 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-purple-600 file:text-white hover:file:bg-purple-500 cursor-pointer"
          />
          {audio && (
            <p className="mt-2 text-xs text-gray-400">
              Selected: <span className="text-purple-400">{audioName}</span>
            </p>
          )}
        </div>


        {/** SearchSong Component */}
        <div className="max-h-60 overflow-y-auto">
          <SearchSong
            onAddSong={(songData) => {
              // receive trimmed song as File
              setAudio(songData.file);
              setAudioName(songData.name); // optional: set caption to song name
              alert(`🎵 Added trimmed song: ${songData.name}`);
            }}
          />
        </div>

        {/* Caption */}
        <div>
          <label className="block text-sm font-medium mb-2">Caption</label>
          <textarea
            placeholder="Write something about your post..."
            value={caption}
            onChange={(e) => setCaption(e.target.value)}
            className="w-full px-3 py-2 rounded-lg bg-gray-800 border border-gray-700 focus:ring-2 focus:ring-blue-500"
            rows="3"
          />
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={loading}
          className="w-full py-2 rounded-lg bg-blue-600 hover:bg-blue-500 transition duration-200 font-semibold disabled:bg-gray-600"
        >
          {loading ? "⏳ Uploading..." : "🚀 Post"}
        </button>
      </form>
    </div>
  );
};

export default CreatePost;
