// src/components/ProgressBar.jsx

const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${minutes.toString().padStart(2, "0")}:${secs
    .toString()
    .padStart(2, "0")}`;
};

function ProgressBar({ duration, progress, startTime, endTime, onTrimDrag }) {
  return (
    <div className="mt-4 p-4 bg-gray-800 border border-gray-200 rounded-lg shadow-lg">
      {/* Progress + Trim Bar */}
      <div
        className="relative w-full h-2 bg-gray-200 rounded-full cursor-pointer overflow-hidden"
        onClick={onTrimDrag}
      >
        {/* Trim window */}
        {duration > 0 && (
          <div
            className="absolute top-0 h-2 bg-blue-500/50 rounded-full pointer-events-none"
            style={{
              left: `${(startTime / duration) * 100}%`,
              width: `${((endTime - startTime) / duration) * 100}%`,
            }}
          ></div>
        )}
        {/* Progress fill */}
        <div
          className="absolute top-0 left-0 h-2 bg-green-500 rounded-full transition-all duration-100 ease-linear"
          style={{ width: `${progress}%` }}
        ></div>
      </div>

      {/* Labels */}
      <div className="flex justify-between text-sm text-gray-200 mt-2">
        <span>
          Clip:{" "}
          <span className="font-medium text-gray-400">
            {formatTime(startTime)}
          </span>{" "}
          to{" "}
          <span className="font-medium text-gray-400">
            {formatTime(endTime)}
          </span>
        </span>
        <span>
          Duration:{" "}
          <span className="font-medium text-gray-400">
            {formatTime(duration)}
          </span>
        </span>
      </div>
    </div>
  );
}

export default ProgressBar;
