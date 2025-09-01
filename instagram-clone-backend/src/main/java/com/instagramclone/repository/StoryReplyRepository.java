package com.instagramclone.repository;

import com.instagramclone.model.Story;
import com.instagramclone.model.StoryReply;
import com.instagramclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryReplyRepository extends JpaRepository<StoryReply, Long> {

    List<StoryReply> findByStoryOrderByRepliedAtDesc(Story story);

    List<StoryReply> findByUser(User user);

    long countByStory(Story story);

	List<StoryReply> findByStory(Story story);
}
