import React from "react";
import useFetch from "../hooks/useFetch";

const Explore = () => {
  const { data: posts, loading, error } = useFetch("http://localhost:8080/api/posts/explore");

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold">Explore</h1>
      {loading && <p>Loading...</p>}
      {error && <p className="text-red-500">{error}</p>}
      <div className="grid grid-cols-3 gap-4">
        {posts?.map((post) => (
          <img key={post.id} src={post.imageUrl} alt="Post" className="w-full" />
        ))}
      </div>
    </div>
  );
};

export default Explore;
