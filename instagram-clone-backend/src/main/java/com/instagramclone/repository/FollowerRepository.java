package com.instagramclone.repository;

import com.instagramclone.model.Follower;
import com.instagramclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
    Optional<Follower> findByFollowerAndFollowing(User follower, User following);
    List<Follower> findByFollowing(User following); // to get followers
    List<Follower> findByFollower(User follower);   // to get following
    
    @Query("SELECT f FROM Follower f WHERE f.following.username = :username OR f.following.username LIKE %:username%")
    List<Follower> searchFollowersByUsername(@Param("username") String username);

    @Query("SELECT f FROM Follower f WHERE f.follower.username = :username OR f.follower.username LIKE %:username%")
    List<Follower> searchFollowingByUsername(@Param("username") String username);

    // ✅ Add this for your privacy check
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    long countByFollowing(User following);
    
    long countByFollower(User follower);

}
