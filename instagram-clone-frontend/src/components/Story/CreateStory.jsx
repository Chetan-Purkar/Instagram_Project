import { useState } from "react";
import { createStory } from "../../api/StoryApi";
import SearchSong from "../song/SearchSong";

const CreateStory = () => {
  const [mediaFile, setMediaFile] = useState(null);
  const [audioFile, setAudioFile] = useState(null);
  const [caption, setCaption] = useState("");
  const [duration, setDuration] = useState(24);
  const [loading, setLoading] = useState(false);
  const [preview, setPreview] = useState(null);

  // Show media preview when selected
  const handleMediaChange = (e) => {
    const file = e.target.files[0];
    setMediaFile(file);
    if (file) {
      const url = URL.createObjectURL(file);
      setPreview(url);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!mediaFile) return alert("Please select a media file.");

    setLoading(true);
    try {
      const token = localStorage.getItem("token"); // JWT token from localStorage
      const storyData = await createStory(
        { mediaFile, audioFile, caption, durationInHours: duration },
        token
      );

      console.log("Story created:", storyData);
      alert("Story uploaded successfully!");

      // Reset form
      setMediaFile(null);
      setAudioFile(null);
      setCaption("");
      setDuration(24);
      setPreview(null);
    } catch (err) {
      console.error(err);
      alert("Failed to create story.");
    }
    setLoading(false);
  };

  return (
    <div className="max-w-md mx-auto mt-4 p-6 bg-gray-900 text-white shadow-lg rounded-lg">
      <h2 className="text-xl font-bold mb-4 text-center">Create a Story</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Media Upload */}
        <div>
          <label className="block text-sm font-medium mb-1">Media (Image/Video)</label>
          <input
            type="file"
            accept="image/*,video/*"
            onChange={handleMediaChange}
            className="w-full text-sm text-gray-300 file:mr-3 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-600 file:text-white hover:file:bg-blue-500 cursor-pointer"
         />
        </div>

        {/* Preview */}
        {preview && (
          <div className="my-2">
            {mediaFile.type.startsWith("image") ? (
              <img src={preview} alt="preview" className="w-[500px] rounded" />
            ) : (
              <video src={preview} controls className="w-[400px] rounded" />
            )}
          </div>
        )}

        {/* Audio Upload */}
        <div>
          <label className="block text-sm font-medium mb-1">Audio (Optional)</label>
          <input
            type="file"
            accept="audio/*"
            onChange={(e) => setAudioFile(e.target.files[0])}
           className="w-full text-sm text-gray-300 file:mr-3 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-purple-600 file:text-white hover:file:bg-purple-500 cursor-pointer"
         />
        </div>

        {/* SearchSong Component */}
        <div className="max-h-60 overflow-y-auto">
          <SearchSong
            onAddSong={(songData) => {
              // receive trimmed song as File
              setAudioFile(songData.file);
              setCaption(songData.name); // optional: set caption to song name
              alert(`🎵 Added trimmed audio: ${songData.name}`);
            }}
          />
        </div>


        {/* Caption */}
        <div>
          <label className="block text-sm font-medium mb-1">Caption</label>
          <input
            type="text"
            value={caption}
            onChange={(e) => setCaption(e.target.value)}
            placeholder="Write something..."
            className="w-full border px-3 py-2 rounded text-gray-900"
          />
        </div>

        {/* Duration */}
        <div>
          <label className="block text-sm font-medium mb-1">Duration (Hours)</label>
          <input
            type="number"
            min={1}
            max={48}
            value={duration}
            onChange={(e) => setDuration(Number(e.target.value))}
            className="w-full border px-3 py-2 rounded text-gray-900"
          />
        </div>

        {/* Submit */}
        <button
          type="submit"
          className={`w-full py-2 px-4 rounded text-white font-semibold ${
            loading ? "bg-gray-400" : "bg-blue-600 hover:bg-blue-700"
          }`}
          disabled={loading}
        >
          {loading ? "Uploading..." : "Create Story"}
        </button>
      </form>
    </div>
  );
};

export default CreateStory;
