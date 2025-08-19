import React, { useEffect, useState } from "react";

const Posts = ({username}) => {
    const [posts, setPosts] = useState([]);

    useEffect(() => {
        fetch(`http://localhost:8080/api/posts/user`)
            .then(response => response.json())
            .then(data => setPosts(data))
            .catch(error => console.error("Error fetching posts:", error));
    }, [username]);

    return (
        <div>
            <h2>Posts by {username}</h2>
            {posts.map(post => (
                <div key={post.id} style={{ border: "1px solid #ccc", padding: "10px", margin: "10px" }}>
                    <img src={post.mediaUrl} alt="Post" style={{ width: "100%" }} />
                    <p>{post.caption}</p>
                    <p><strong>Likes:</strong> {post.likes.length}</p>
                    <h4>Comments:</h4>
                    <ul>
                        {post.comments.map(comment => (
                            <li key={comment.id}>{comment.text} - <strong>{comment.user.username}</strong></li>
                        ))}
                    </ul>
                </div>
            ))}
        </div>
    );
};

export default Posts;
