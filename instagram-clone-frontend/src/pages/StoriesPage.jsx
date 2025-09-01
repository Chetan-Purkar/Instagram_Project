import MyStories from "../components/Story/MyStories";
import FollowingStories from "../components/Story/FollowingStories";    

const StoriesPage = () => {
  return (
    <div className="flex flex-row items-center p-8 m-4">
      {/* My Stories Section */}
      <div className="mb-6">
        <MyStories />
      </div>

      {/* Following Stories Section */}
      <div className="mb-6">
        <FollowingStories />
      </div>
    </div>
  );
};

export default StoriesPage;
