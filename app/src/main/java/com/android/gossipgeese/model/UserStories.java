package com.android.gossipgeese.model;

public class UserStories {
    String image;

    public String getImage() {
        return image;
    }

    public UserStories() {
    }

    public UserStories(String image, long storyAT) {
        this.image = image;
        this.storyAT = storyAT;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStoryAT() {
        return storyAT;
    }

    public void setStoryAT(long storyAT) {
        this.storyAT = storyAT;
    }

    long storyAT;

}
