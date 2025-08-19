import { useEffect, useRef } from "react";

const AudioPlayer = ({ audioUrl, isActive, muted, setMuted }) => {
  const audioRef = useRef(null);

  useEffect(() => {
    if (!audioRef.current) return;

    if (isActive) {
      audioRef.current.muted = muted;
      audioRef.current.loop = true;
      audioRef.current.play().catch(() => {});
    } else {
      audioRef.current.pause();
    }
  }, [isActive, muted]);

  return (
    <div className="flex items-center justify-end w-full">
      {/* Keep audio hidden but functional */}
      <audio ref={audioRef} src={audioUrl} playsInline className="hidden" />

      {/* Mute/Unmute button aligned to end (right) */}
      <button
        onClick={() => setMuted((prev) => !prev)}
        className="text-blue-500 mr-2 "
      >
        {muted ? "🔇" : "🔊"}
      </button>
    </div>
  );
};

export default AudioPlayer;
