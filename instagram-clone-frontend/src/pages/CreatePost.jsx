import React, { useState } from "react";
import { createPost } from "../api/PostApi";

const CreatePost = () => {
  const [media, setMedia] = useState(null);
  const [audio, setAudio] = useState(null);
  const [caption, setCaption] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleFileChange = (event) => {
    setMedia(event.target.files[0]);
  };

  const handleAudioChange = (event) => {
    setAudio(event.target.files[0]);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!media) {
      setError("Please select an image or video.");
      return;
    }

    const formData = new FormData();
    formData.append("mediaData", media);
    formData.append("mediaType", media.type);
    formData.append("caption", caption);
    if (audio) {
      formData.append("audioData", audio);
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const token = localStorage.getItem("token"); // Retrieve token if needed
      await createPost(formData, token);
      setSuccess("Post uploaded successfully!");
      setCaption("");
      setMedia(null);
      setAudio(null);
    } catch (err) {
      setError(err || "Error uploading post.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto p-4 border rounded-lg shadow-lg bg-white">
      <h2 className="text-xl font-bold mb-4">Create a Post</h2>
      {error && <p className="text-red-500">{error}</p>}
      {success && <p className="text-green-500">{success}</p>}
      <form onSubmit={handleSubmit}>
        <label htmlFor="media-upload">Upload Media </label>
        <input type="file" id="media-upload" placeholder="add post" accept="image/*,video/*" onChange={handleFileChange} className="mb-2" />
        <label htmlFor="audio-upload">Upload Audio </label>
        <input type="file" id="audio-upload" placeholder="add audio" accept="audio/*" onChange={handleAudioChange} className="mb-2" />
        <textarea
          placeholder="Enter caption..."
          value={caption}
          onChange={(e) => setCaption(e.target.value)}
          className="w-full p-2 border rounded"
        />
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-500 text-white p-2 rounded mt-2"
        >
          {loading ? "Uploading..." : "Post"}
        </button>
      </form>
    </div>
  );
};

export default CreatePost;
