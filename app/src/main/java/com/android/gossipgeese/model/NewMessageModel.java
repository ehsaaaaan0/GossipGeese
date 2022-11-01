package com.android.gossipgeese.model;

public class NewMessageModel {
    String id, name, image, lastMsg, time, recent, archive;


    public NewMessageModel(){}

    public NewMessageModel(String id, String name, String image, String lastMsg, String time, String recent, String archive) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.lastMsg = lastMsg;
        this.time = time;
        this.recent = recent;
        this.archive = archive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRecent() {
        return recent;
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }
}
