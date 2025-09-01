package com.instagramclone.repository;

import com.instagramclone.model.Story;
import com.instagramclone.model.StoryLike;
import com.instagramclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryLikeRepository extends JpaRepository<StoryLike, Long> {

    Optional<StoryLike> findByStoryAndUser(Story story, User user);

    List<StoryLike> findByStory(Story story);

    long countByStory(Story story);

    void deleteByStoryAndUser(Story story, User user);
}
