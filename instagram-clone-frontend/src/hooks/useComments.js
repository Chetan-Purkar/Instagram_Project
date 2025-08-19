// import { useEffect, useState } from "react";
// import {
//   fetchComments,
//   addComment,
//   deleteComment,
// } from "../api/CommentApi";

// const useComments = (postId, username) => {
//   const [comments, setComments] = useState([]);
//   const [commentText, setCommentText] = useState("");
//   const [loading, setLoading] = useState(true);

//   useEffect(() => {
//     const loadComments = async () => {
//       try {
//         const data = await fetchComments(postId);
//         setComments(data);
//       } catch (error) {
//         console.error("Error loading comments:", error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     if (postId) {
//       loadComments();
//     }
//   }, [postId]);

//   const handleCommentChange = (e) => {
//     setCommentText(e.target.value);
//   };

//   const handleAddComment = async () => {
//     if (!commentText.trim()) return;

//     try {
//       const newComment = await addComment(postId, username, commentText);
//       setComments((prev) => [...prev, newComment]);
//       setCommentText("");
//     } catch (error) {
//       console.error("Failed to add comment:", error);
//     }
//   };

//   const handleDeleteComment = async (commentId) => {
//     try {
//       await deleteComment(commentId, username);
//       setComments((prev) => prev.filter((comment) => comment.id !== commentId));
//     } catch (error) {
//       console.error("Failed to delete comment:", error);
//     }
//   };

//   return {
//     comments,
//     loading,
//     commentText,
//     handleCommentChange,
//     handleAddComment,
//     handleDeleteComment,
//   };
// };

// export default useComments;
