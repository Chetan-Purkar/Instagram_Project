import { useEffect, useState, useContext, useRef, useCallback } from "react";
import { ThemeContext } from "../context/ThemeContext";
import { fetchPosts } from "../api/PostApi";
import { fetchComments } from "../api/CommentApi";
import moment from "moment";
import { Link } from "react-router-dom";
import CommentBox from "../components/comment/CommentBox";
import LikeButton from "../components/LikeButton";
import PostAudioPlayer from "../components/PostAudioPlayer";
import StoriesPage from "./StoriesPage";

const Home = () => {
  const { theme } = useContext(ThemeContext);

  const [posts, setPosts] = useState([]);
  const [showCommentsPopup, setShowCommentsPopup] = useState(null);
  const [likesByPost, setLikesByPost] = useState({});
  const [isFetchingPosts, setIsFetchingPosts] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const currentUser = useRef(localStorage.getItem("username")).current;

  // 👇 Separate maps:
  // viewRefs = visible sentinels we observe
  // mediaRefs = actual <video> or <audio> elements we control
  const viewRefs = useRef({});
  const mediaRefs = useRef({});

  const observerRef = useRef(null);

  // Fetch paginated posts (dedupe)
  const fetchPaginatedPosts = useCallback(async () => {
    if (isFetchingPosts || page >= totalPages) return;

    setIsFetchingPosts(true);
    try {
      const { posts: newPosts, totalPages: total } = await fetchPosts(page, 5);

      setPosts((prev) => {
        const existing = new Set(prev.map((p) => p.id));
        const filtered = newPosts.filter((p) => !existing.has(p.id));
        return [...prev, ...filtered];
      });

      const likesData = {};
      await Promise.all(
        newPosts.map(async (post) => {
          likesData[post.id] = {
            liked: post.likedByCurrentUser ?? post.likes?.includes(currentUser),
            count: post.likes?.length ?? post.likesCount ?? 0,
          };
          await fetchComments(post.id);
        })
      );
      setLikesByPost((prev) => ({ ...prev, ...likesData }));

      setTotalPages(total);
      setPage((prev) => prev + 1);
    } catch (err) {
      console.error("Error fetching posts:", err);
    } finally {
      setIsFetchingPosts(false);
    }
  }, [page, totalPages, isFetchingPosts, currentUser]);

  // Initial fetch
  useEffect(() => {
    fetchPaginatedPosts();
  }, [fetchPaginatedPosts]);

  // Infinite scroll: watch the last post container
  useEffect(() => {
    if (observerRef.current) observerRef.current.disconnect();

    observerRef.current = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) fetchPaginatedPosts();
        });
      },
      { threshold: 1 }
    );

    const last = posts[posts.length - 1];
    if (!last) return;

    const lastEl = document.querySelector(`#post-${last.id}`);
    if (lastEl) observerRef.current.observe(lastEl);

    return () => observerRef.current?.disconnect();
  }, [posts, fetchPaginatedPosts]);

  const timeAgo = (date) => moment(date).fromNow();
  const toggleCommentsPopup = (postId) =>
    setShowCommentsPopup((prev) => (prev === postId ? null : postId));

  // ---- Playback control ----
  const [currentPlayingPostId, setCurrentPlayingPostId] = useState(null);
  const [globalMuted, setGlobalMuted] = useState(true);

  // Observe VISIBLE wrappers, not the hidden audio tag
  useEffect(() => {
    const snapshot = { ...viewRefs.current };
    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const pid = entry.target.getAttribute("data-postid");
            if (pid) {
              setCurrentPlayingPostId(pid);
              // optional logging
              // console.log("🔎 Active post in view:", pid);
            }
          }
        });
      },
      { threshold: 0.6 } // a bit looser than 0.9 so it triggers earlier
    );

    Object.values(snapshot).forEach((el) => el && io.observe(el));

    return () => {
      Object.values(snapshot).forEach((el) => el && io.unobserve(el));
    };
  }, [posts]);

  // Video playback is controlled here.
  // (Audio is handled inside PostAudioPlayer via isActive + muted props)
  useEffect(() => {
    Object.entries(mediaRefs.current).forEach(([postId, el]) => {
      if (!el) return;
      if (el.tagName === "VIDEO") {
        if (currentPlayingPostId === postId) {
          el.muted = globalMuted;
          el.play().catch(() => {});
        } else {
          el.pause();
        }
      }
      // No need to handle AUDIO here; PostAudioPlayer does it.
    });
  }, [currentPlayingPostId, globalMuted]);

  return (
    <div
      className={`flex flex-col items-center min-h-screen ${
        theme === "dark" ? "bg-gray-900 text-white" : "bg-white text-black"
      }`}
    >
      <StoriesPage />
      <h2 className="text-3xl font-bold mb-6">Latest Posts</h2>

      {posts.map((post) => {
        const {
          id,
          username,
          createdAt,
          mediaData,
          mediaType,
          caption,
          likesCount,
          commentsCount,
          profileImage,
          audioName,
          audioUrl,
        } = post;

        const likeInfo =
          likesByPost[id] ?? { liked: false, count: likesCount ?? 0 };

        const uniqueKey = String(id); // stable key and id for maps

        return (
          <div
            key={uniqueKey}
            id={`post-${id}`}
            className={`w-full max-w-md p-5 mb-6 rounded-lg shadow-lg ${
              theme === "dark"
                ? "bg-gray-800 border border-gray-700"
                : "bg-gray-100 border border-gray-300"
            }`}
          >
            <div className="flex justify-between items-center mb-3">
              <div className="flex items-center space-x-3">
                <img
                  src={
                    profileImage
                      ? `data:image/png;base64,${profileImage}`
                      : "placeholder.png"
                  }
                  alt="Profile"
                  className="w-10 h-10 rounded-full border border-gray-400"
                />
                <div className="flex flex-col">
                  <Link to={`/${username}`} className="font-semibold">
                    {username}
                  </Link>
                  {audioName && (
                    <p className="text-xs text-gray-500 overflow-hidden whitespace-nowrap w-40 text-ellipsis">
                      🎵 {audioName}
                    </p>
                  )}
                </div>
              </div>
              <span className="text-sm text-gray-500">{timeAgo(createdAt)}</span>
            </div>

            {/* 👇 Visible media wrapper acts as the sentinel for visibility */}
            <div
              className="w-full mb-3 relative"
              data-postid={uniqueKey}
              ref={(el) => (viewRefs.current[uniqueKey] = el)}
            >
              {mediaType?.includes("image") && (
                <img
                  src={`data:${mediaType};base64,${mediaData}`}
                  alt="Post"
                  className="w-full max-h-[600px] rounded-md object-cover"
                />
              )}

              {mediaType?.includes("video") && (
                <video
                  ref={(el) => (mediaRefs.current[uniqueKey] = el)}
                  className="w-full rounded-md max-h-[600px] object-cover"
                  playsInline
                  muted={globalMuted}
                  loop
                >
                  <source
                    src={`data:${mediaType};base64,${mediaData}`}
                    type={mediaType}
                  />
                </video>
              )}

              {audioUrl && (
                <PostAudioPlayer
                  ref={(el) => (mediaRefs.current[uniqueKey] = el)} // actual <audio>
                  audioUrl={audioUrl}
                  isActive={currentPlayingPostId === uniqueKey} // driven by wrapper visibility
                  muted={globalMuted}
                  setMuted={setGlobalMuted}
                />
              )}
            </div>

            <p>{caption}</p>

            <div className="flex justify-between mt-3 text-sm text-blue-500">
              <LikeButton
                postId={id}
                likeInfo={likeInfo}
                setLikesByPost={setLikesByPost}
              />
              <button onClick={() => toggleCommentsPopup(id)}>
                {commentsCount} Comments
              </button>
              <button>Share</button>
            </div>

            {showCommentsPopup === id && (
              <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg w-full max-w-md max-h-[70vh] flex flex-col relative overflow-hidden">
                  <button
                    onClick={() => setShowCommentsPopup(null)}
                    className="absolute top-2 right-3 text-red-500 text-xl"
                  >
                    ×
                  </button>
                  <CommentBox postId={id} currentUser={currentUser} />
                </div>
              </div>
            )}
          </div>
        );
      })}

      {isFetchingPosts && (
        <p className="text-gray-500 mb-4">Loading more posts...</p>
      )}
    </div>
  );
};

export default Home;
