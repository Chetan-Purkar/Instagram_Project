import { useState, useRef } from "react";
import { searchSongs } from "../../api/JiosaavnApi";
import ProgressBar from "./ProgressBar";

function SearchSong({ onAddSong }) {
  const [query, setQuery] = useState("");
  const [songs, setSongs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const audioRefs = useRef({});

  const handleSearch = async (reset = false) => {
    if (!query.trim()) return;
    try {
      setLoading(true);
      const currentPage = reset ? 1 : page;
      const res = await searchSongs(query, currentPage);
      const results = res?.data?.results || res?.results || [];

      const mapped = results.map((song) => ({
        ...song,
        isPlaying: false,
        startTime: 0,
        endTime: Math.min(30, song.duration || 30),
        progress: 0,
        duration: song.duration || 0,
      }));

      if (reset) {
        setSongs(mapped);
        setPage(2);
        setHasMore(mapped.length > 0);
      } else {
        setSongs((prev) => [...prev, ...mapped]);
        setPage((prev) => prev + 1);
        if (mapped.length === 0) setHasMore(false);
      }
    } catch (err) {
      console.error("Error fetching songs:", err);
    } finally {
      setLoading(false);
    }
  };

  const togglePlay = (songId) => {
    setSongs((prev) =>
      prev.map((s) => {
        if (s.id === songId) {
          const audio = audioRefs.current[s.id];
          if (!audio) return s;

          if (s.isPlaying) {
            audio.pause();
            return { ...s, isPlaying: false };
          } else {
            audio.play();
            return { ...s, isPlaying: true };
          }
        } else {
          const audio = audioRefs.current[s.id];
          if (audio) audio.pause();
          return { ...s, isPlaying: false };
        }
      })
    );
  };

  const handleTimeUpdate = (songId) => {
    setSongs((prev) =>
      prev.map((s) => {
        if (s.id === songId) {
          const audio = audioRefs.current[s.id];
          if (!audio) return s;

          if (audio.currentTime >= s.endTime) {
            audio.currentTime = s.startTime;
            audio.play();
          }

          return {
            ...s,
            progress: (audio.currentTime / (s.duration || 1)) * 100,
          };
        }
        return s;
      })
    );
  };

  const handleLoadedMeta = (songId) => {
    const audio = audioRefs.current[songId];
    if (!audio) return;

    setSongs((prev) =>
      prev.map((s) =>
        s.id === songId
          ? {
              ...s,
              duration: audio.duration,
              startTime: 0,
              endTime: Math.min(30, audio.duration),
            }
          : s
      )
    );
  };

  const handleTrimDrag = (e, songId) => {
    const bar = e.currentTarget.getBoundingClientRect();
    const clickX = e.clientX - bar.left;
    const percent = clickX / bar.width;

    setSongs((prev) =>
      prev.map((s) => {
        if (s.id === songId) {
          const newStart = percent * s.duration;
          return {
            ...s,
            startTime: newStart,
            endTime: Math.min(newStart + 30, s.duration),
          };
        }
        return s;
      })
    );

    if (audioRefs.current[songId])
      audioRefs.current[songId].currentTime =
        percent * (songs.find((x) => x.id === songId)?.duration || 0);
  };

  const addSong = async (song) => {
    const audio = audioRefs.current[song.id];
    if (!audio) return;

    try {
      const response = await fetch(
        song.downloadUrl?.find((d) => d.quality === "320kbps")?.url
      );
      const arrayBuffer = await response.arrayBuffer();
      const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      const decoded = await audioCtx.decodeAudioData(arrayBuffer);

      const start = song.startTime;
      const end = song.endTime;
      const sampleRate = decoded.sampleRate;
      const frameCount = (end - start) * sampleRate;

      const trimmedBuffer = audioCtx.createBuffer(
        decoded.numberOfChannels,
        frameCount,
        sampleRate
      );

      for (let i = 0; i < decoded.numberOfChannels; i++) {
        const channelData = decoded
          .getChannelData(i)
          .slice(start * sampleRate, end * sampleRate);
        trimmedBuffer.copyToChannel(channelData, i);
      }

      const offlineCtx = new OfflineAudioContext(
        trimmedBuffer.numberOfChannels,
        trimmedBuffer.length,
        trimmedBuffer.sampleRate
      );
      const source = offlineCtx.createBufferSource();
      source.buffer = trimmedBuffer;
      source.connect(offlineCtx.destination);
      source.start();
      const renderedBuffer = await offlineCtx.startRendering();

      const wavBlob = bufferToWave(renderedBuffer, renderedBuffer.length);
      const file = new File([wavBlob], `${song.name}.wav`, {
        type: "audio/wav",
      });

     

      // ✅ call parent callback if provided
      if (typeof onAddSong === "function") {
        onAddSong({ file, name: song.name });
      }

      alert(`✅ Added trimmed song: ${song.name}`);
    } catch (err) {
      console.error("Error trimming:", err);
    }
  };

  const bufferToWave = (abuffer, len) => {
    const numOfChan = abuffer.numberOfChannels;
    const length = len * numOfChan * 2 + 44;
    const buffer = new ArrayBuffer(length);
    const view = new DataView(buffer);
    const channels = [];
    let i;
    let sample;
    let offset = 0;
    let pos = 0;

    setUint32(0x46464952);
    setUint32(length - 8);
    setUint32(0x45564157);
    setUint32(0x20746d66);
    setUint32(16);
    setUint16(1);
    setUint16(numOfChan);
    setUint32(abuffer.sampleRate);
    setUint32(abuffer.sampleRate * 2 * numOfChan);
    setUint16(numOfChan * 2);
    setUint16(16);
    setUint32(0x61746164);
    setUint32(length - pos - 4);

    for (i = 0; i < abuffer.numberOfChannels; i++)
      channels.push(abuffer.getChannelData(i));

    while (pos < length) {
      for (i = 0; i < numOfChan; i++) {
        sample = Math.max(-1, Math.min(1, channels[i][offset]));
        sample = (0.5 + sample * 32767) | 0;
        view.setInt16(pos, sample, true);
        pos += 2;
      }
      offset++;
    }

    return new Blob([buffer], { type: "audio/wav" });

    function setUint16(data) {
      view.setUint16(pos, data, true);
      pos += 2;
    }
    function setUint32(data) {
      view.setUint32(pos, data, true);
      pos += 4;
    }
  };

  return (
    <div className="p-4 max-w-xl mx-auto font-sans relative">

      {/* Search Bar */}
      <div className="sticky top-0 bg-gray-900 z-10 p-2 mb-4">
        <div className="flex gap-2">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch(true)}
            placeholder="Search for songs..."
            className="flex-1 py-1 px-2 border border-gray-300 rounded-lg 
                      focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900"
          />
          <button
            type="button"
            onClick={() => handleSearch(true)}
            className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700"
          >
            Search
          </button>
        </div>
      </div>


      {/* Results */}
      <ul className="divide-y divide-gray-500">
        {songs.map((song) => {
          const audioUrl = song.downloadUrl?.find(
            (d) => d.quality === "320kbps"
          )?.url;
          return (
            <li
              key={song.id}
              className="bg-gray-800 p-2 rounded-lg shadow-sm mb-3"
            >
              <div className="flex items-center gap-4">
                {song.image?.[2]?.url && (
                  <img
                    src={song.image[2].url}
                    alt={song.name}
                    className="w-12 h-12 rounded-full object-cover"
                  />
                )}
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-100 truncate">
                    {song.name}
                  </p>
                  <p className="text-sm text-gray-400 truncate">
                    🎤 {song.artists?.all?.map((a) => a.name).join(", ")}
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => togglePlay(song.id)}
                  className="bg-green-600 text-white px-3 py-1 rounded-full"
                >
                  {song.isPlaying ? "⏸" : "▶"}
                </button>
                <button
                  type="button"
                  onClick={() => addSong(song)}
                  className="bg-purple-600 text-white px-2 py-1 rounded-full"
                >
                  ➕
                </button>
              </div>

              {/* Progress + Trimmer */}
              {audioUrl && (
                <div className="mt-2">
                  <audio
                    ref={(el) => (audioRefs.current[song.id] = el)}
                    src={audioUrl}
                    onTimeUpdate={() => handleTimeUpdate(song.id)}
                    onLoadedMetadata={() => handleLoadedMeta(song.id)}
                  />
                  <ProgressBar
                    duration={song.duration}
                    progress={song.progress}
                    startTime={song.startTime}
                    endTime={song.endTime}
                    onTrimDrag={(e) => handleTrimDrag(e, song.id)}
                  />
                </div>
              )}
            </li>
          );
        })}
      </ul>

      {loading && <p className="text-center text-gray-500">Loading...</p>}
      {!loading && hasMore && songs.length > 0 && (
        <div className="flex justify-center mt-4">
          <button
            type="button"
            onClick={() => handleSearch(false)}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Load More
          </button>
        </div>
      )}
    </div>
  );
}

export default SearchSong;
