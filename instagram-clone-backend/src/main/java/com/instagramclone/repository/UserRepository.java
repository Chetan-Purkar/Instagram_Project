package com.instagramclone.repository;

import com.instagramclone.model.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	
    boolean existsByUsername(String username);
//    List<User> findAll();
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.posts WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    
    @Query("SELECT u FROM User u " +
    	       "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
    	       "ORDER BY CASE WHEN LOWER(u.username) LIKE LOWER(CONCAT(:query, '%')) THEN 0 ELSE 1 END, u.username")
    List<User> searchUsersByUsernamePriority(String query);

    @EntityGraph(attributePaths = "blockedUsers")
    Optional<User> findWithBlockedUsersById(Long id);

  
}
