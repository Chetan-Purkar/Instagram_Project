import { useEffect, useRef } from "react";

const AudioPlayer = ({ audioUrl, isActive, muted, setMuted }) => {
  const audioRef = useRef(null);

  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;

    audio.muted = muted;
    audio.loop = true;

    if (isActive) {
      audio.play().catch((err) => {
        console.log("Audio play failed:", err);
      });
    } else {
      audio.pause();
    }
  }, [isActive, muted, audioUrl]);

  return (
    <div className="flex items-center justify-center w-full">
      <audio ref={audioRef} src={audioUrl} playsInline className="hidden" />
      <button
        onClick={() => setMuted((prev) => !prev)}
        className="text-white ml-2"
      >
        {muted ? "🔇" : "🔊"}
      </button>
    </div>
  );
};

export default AudioPlayer;
