package com.instagramclone.dto;

public class LikeToggleResponse {
    private boolean previouslyLiked;
    private boolean currentlyLiked;

    // Constructor
    public LikeToggleResponse(boolean previouslyLiked, boolean currentlyLiked) {
        this.previouslyLiked = previouslyLiked;
        this.currentlyLiked = currentlyLiked;
    }

    // Getters and Setters
    public boolean isPreviouslyLiked() {
        return previouslyLiked;
    }

    public boolean isCurrentlyLiked() {
        return currentlyLiked;
    }
}
