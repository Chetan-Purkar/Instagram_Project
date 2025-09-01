import { useEffect, useState, useContext, useRef } from "react";
import { ThemeContext } from "../context/ThemeContext";
import { fetchAllPosts } from "../api/PostApi";
import { fetchComments } from "../api/CommentApi";
import moment from "moment";
import { Link } from "react-router-dom";
import CommentBox from "../components/comment/CommentBox";
import LikeButton from "../components/LikeButton";
import AudioPlayer from "../components/AudioPlayer";
import StoriesPage from "./StoriesPage";

const Home = () => {
  const { theme } = useContext(ThemeContext);
  const [posts, setPosts] = useState([]);
  const [showCommentsPopup, setShowCommentsPopup] = useState(null);
  const [likesByPost, setLikesByPost] = useState({});
  const [isFetchingPosts, setIsFetchingPosts] = useState(true);

  const currentUser = useRef(localStorage.getItem("username")).current;

  useEffect(() => {
    const fetchData = async () => {
      setIsFetchingPosts(true);
      try {
        const data = await fetchAllPosts();

        const sortedData = data.sort(
          (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
        );

        const likesData = {};
        const commentsData = {};

        await Promise.all(
          sortedData.map(async (post) => {
            likesData[post.id] = {
              liked:
                post.likedByCurrentUser ??
                post.likes?.includes(currentUser),
              count: post.likes?.length ?? post.likesCount ?? 0,
            };
            commentsData[post.id] = await fetchComments(post.id);
          })
        );

        setPosts(sortedData);
        setLikesByPost(likesData);
      } catch (error) {
        console.error("Error fetching posts:", error);
      } finally {
        setIsFetchingPosts(false);
      }
    };

    fetchData();
  }, [currentUser]);

  const timeAgo = (date) => moment(date).fromNow();

  const toggleCommentsPopup = (postId) => {
    setShowCommentsPopup((prev) => (prev === postId ? null : postId));
  };

  // 🔊 Audio/Video control states
  const [currentPlayingPostId, setCurrentPlayingPostId] = useState(null);
  const [globalMuted, setGlobalMuted] = useState(true);
  const postRefs = useRef({});

  // 👀 Observe post visibility
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setCurrentPlayingPostId(entry.target.dataset.postid);
          }
        });
      },
      { threshold: 0.9 }
    );

    const refsSnapshot = Object.values(postRefs.current);

    refsSnapshot.forEach((ref) => {
      if (ref) observer.observe(ref);
    });

    return () => {
      refsSnapshot.forEach((ref) => {
        if (ref) observer.unobserve(ref);
      });
    };
  }, [posts]);

  // 🎥 Control audio & video playback
  useEffect(() => {
    Object.entries(postRefs.current).forEach(([postId, el]) => {
      if (!el) return;

      if (el.tagName === "VIDEO") {
        if (currentPlayingPostId === postId) {
          el.muted = globalMuted;
          el.play().catch(() => {});
        } else {
          el.pause();
        }
      }

      if (el.tagName === "AUDIO") {
        if (currentPlayingPostId === postId) {
          el.muted = globalMuted;
          el.play().catch(() => {});
        } else {
          el.pause();
        }
      }
    });
  }, [currentPlayingPostId, globalMuted]);

  return (
    <div
      className={`flex flex-col items-center min-h-screen transition-all duration-300 ${
        theme === "dark"
          ? "bg-gray-900 text-white"
          : "bg-white text-black"
      }`}
    >
      <StoriesPage />

      <h2 className="text-3xl font-bold mb-6">Latest Posts</h2>

      {isFetchingPosts ? (
        <p className="text-gray-500">Loading posts...</p>
      ) : posts.length === 0 ? (
        <p className="text-gray-500">No posts found.</p>
      ) : (
        posts.map((post) => {
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
          } = post;

          const likeInfo =
            likesByPost[id] ?? { liked: false, count: likesCount ?? 0 };

          return (
            <div
              key={id}
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
                     <Link
                        to={`/${username}`}
                        className="font-semibold"
                      >
                        {username}
                      </Link>
                      {/* audioName overflow to add three dots no last */}
                      {post.audioName && (
                        <p className="text-xs text-gray-500 overflow-hidden whitespace-nowrap w-40 text-ellipsis">🎵 {post.audioName}</p>
                      )}
                  </div>
                 
                </div>
                <span className="text-sm text-gray-500">
                  {timeAgo(createdAt)}
                </span>
              </div>

              <div className="w-full mb-3 relative">
                {mediaType?.includes("image") && (
                  <img
                    src={`data:${mediaType};base64,${mediaData}`}
                    alt="Post"
                    className="w-full max-h-[600px] rounded-md object-cover"
                  />
                )}
                {mediaType?.includes("video") && (
                  <video
                    ref={(el) => (postRefs.current[id] = el)}
                    data-postid={id}
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

                {/* ✅ Post wrapper (for audio posts only) */}
                <div
                  key={id}
                  data-postid={id}
                  ref={(el) => {
                    if (!mediaType?.includes("video")) {
                      postRefs.current[id] = el;
                    }
                  }}
                  className={`w-full max-w-md ${
                    theme === "dark" ? "bg-gray-800" : "bg-gray-100"
                  }`}
                >
                  {post.audioUrl && (
                    <div className="absolute bottom-2 right-2">
                      <AudioPlayer
                        audioUrl={post.audioUrl}
                        isActive={
                          currentPlayingPostId === String(id)
                        }
                        muted={globalMuted}
                        setMuted={setGlobalMuted}
                      />
                    </div>
                  )}
                </div>
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

                    <CommentBox
                      postId={id}
                      currentUser={currentUser}
                    />
                  </div>
                </div>
              )}    
            </div>
          );
        })
      )}
    </div>
  );
};

export default Home;
