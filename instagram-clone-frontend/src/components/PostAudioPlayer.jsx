import { useEffect, useRef, forwardRef } from "react";

const PostAudioPlayer = forwardRef(
  ({ audioUrl, isActive, muted, setMuted }, ref) => {
    const audioRef = useRef(null);

    // expose the <audio> to parent maps
    useEffect(() => {
      if (ref) {
        // React may pass a function or a ref object
        if (typeof ref === "function") ref(audioRef.current);
        else ref.current = audioRef.current;
      }
    }, [ref]);

    useEffect(() => {
      const audio = audioRef.current;
      if (!audio) return;

      audio.muted = muted;
      audio.loop = true;

      if (isActive) {
        const p = audio.play();
        if (p && typeof p.then === "function") {
          p.then(() => {
            // console.log("🎵 Playing:", audioUrl);
          }).catch((err) => {
            console.log("Audio play failed:", err);
          });
        }
      } else {
        audio.pause();
        audio.currentTime = 0;
      }
    }, [isActive, muted, audioUrl]);

    return (
      <div className="absolute bottom-2 right-2 flex items-center">
        {/* keep audio visible or hidden; visibility doesn't affect observer now */}
        <audio ref={audioRef} src={audioUrl} playsInline className="hidden" />
        <button
          onClick={() => setMuted((prev) => !prev)}
          className="ml-2 bg-gray-700 text-white px-3 py-1 rounded"
          aria-label={muted ? "Unmute audio" : "Mute audio"}
        >
          {muted ? "🔇" : "🔊"}
        </button>
      </div>
    );
  }
);

export default PostAudioPlayer;
