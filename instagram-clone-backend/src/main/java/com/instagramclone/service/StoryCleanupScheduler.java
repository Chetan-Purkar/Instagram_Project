//package com.instagramclone.service;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.instagramclone.model.Story;
//import com.instagramclone.repository.StoryRepository;
//
//@Component
//public class StoryCleanupScheduler {
//
//    private final StoryRepository storyRepository;
//
//    public StoryCleanupScheduler(StoryRepository storyRepository) {
//        this.storyRepository = storyRepository;
//    }
//
//    // Run every 1 hour (3600000 ms)
//    @Scheduled(fixedRate = 3600000)
//    public void deleteExpiredStories() {
//        LocalDateTime now = LocalDateTime.now();
//        List<Story> expiredStories = storyRepository.deleteByExpiryAtBefore(now);
//
//        for (Story story : expiredStories) {
//            // delete file from uploads
//            if (story.getMediaUrl() != null) {
//                Path filePath = Paths.get("uploads/stories/" + 
//                           story.getMediaUrl().substring(story.getMediaUrl().lastIndexOf("/") + 1));
//                try {
//                    Files.deleteIfExists(filePath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            storyRepository.delete(story);
//        }
//    }
//}
