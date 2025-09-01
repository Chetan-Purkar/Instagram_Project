package com.instagramclone.repository;

import com.instagramclone.model.Story;
import com.instagramclone.model.StoryView;
import com.instagramclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryViewRepository extends JpaRepository<StoryView, Long> {

	List<StoryView> findByStoryAndViewer(Story story, User viewer);
    List<StoryView> findByStory(Story story);
    long countByStory(Story story);
}
