package com.android.gossipgeese.model;

import java.util.ArrayList;

public class StoryModel {
    String storyBy;
    long storyAt;
    ArrayList<UserStories> stories;

    public StoryModel() {
    }

    public StoryModel(String storyBy, long storyAt, ArrayList<UserStories> stories) {
        this.storyBy = storyBy;
        this.storyAt = storyAt;
        this.stories = stories;
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.stories = stories;
    }
}
