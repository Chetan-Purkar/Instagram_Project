package com.instagramclone.service;

import com.instagramclone.dto.UserDTO;
import com.instagramclone.enums.AccountPrivacy;
import com.instagramclone.enums.FollowStatus;
import com.instagramclone.model.Follower;
import com.instagramclone.model.User;
import com.instagramclone.repository.FollowerRepository;
import com.instagramclone.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    public UserService(UserRepository userRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        
    }

    // ✅ Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    
    public List<User> getAllUsers() {
        return userRepository.findAll();  // Returning the entire list of users
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public Map<String, Object> updateUser(Long userId, String name, String email, String bio, MultipartFile profileImage) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Store previous details
        Map<String, Object> response = new HashMap<>();
        response.put("previous", new UserDTO(user));

        // Update user details
        user.setName(name);
        user.setEmail(email);
        user.setBio(bio);

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                user.setProfileImage(profileImage.getBytes()); // Directly set the byte array
            } catch (IOException e) {
                throw new IOException("Could not read profile image data", e);
            }
        }

        User updatedUser = userRepository.save(user);

        // Store updated details
        response.put("updated", new UserDTO(updatedUser));

        return response;
    }
    public Optional<User> getUserByUsername(String username) {
    	
        return userRepository.findByUsername(username); // Assumes findByUsername exists in UserRepository
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null); // Fetch user by ID, return null if not found
    }
    
    
    public List<User> searchUsers(String query) {
        return userRepository.searchUsersByUsernamePriority(query);
    }

    public User updatePrivacy(String currentUsername, AccountPrivacy privacy) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountPrivacy(privacy);
        return userRepository.save(user);
    }


    public boolean canViewProfile(User targetUser, User viewer) {
        // Public account → anyone can view
        if (targetUser.getAccountPrivacy() == AccountPrivacy.PUBLIC) {
            return true;
        }

        // Private account
        if (targetUser.getAccountPrivacy() == AccountPrivacy.PRIVATE) {
            // User can view their own profile
            if (targetUser.getId().equals(viewer.getId())) {
                return true;
            }

            // Followers can view only if status is ACCEPTED
            Optional<Follower> relation = followerRepository.findByFollowerAndFollowing(viewer, targetUser);
            return relation.isPresent() && relation.get().getStatus() == FollowStatus.ACCEPTED;
        }

        return false;
    }



   
}
