package com.instagramclone.dto;

public class LikeToggleResponse {
    private boolean previouslyLiked;
    private boolean currentlyLiked;

    public LikeToggleResponse(boolean previouslyLiked, boolean currentlyLiked) {
        this.previouslyLiked = previouslyLiked;
        this.currentlyLiked = currentlyLiked;
    }

    public boolean isPreviouslyLiked() { return previouslyLiked; }
    public boolean isCurrentlyLiked() { return currentlyLiked; }
}
