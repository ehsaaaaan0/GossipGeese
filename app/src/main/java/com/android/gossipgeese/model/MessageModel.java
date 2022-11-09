package com.android.gossipgeese.model;

public class MessageModel {
    String id, msgKey,image,time,message,reaction,voice,type;

    public MessageModel() {
    }

    public MessageModel(String id, String msgKey, String voice, String time, String message, String reaction, String type) {
        this.id = id;
        this.msgKey = msgKey;
        this.voice = voice;
        this.time = time;
        this.message = message;
        this.reaction = reaction;
        this.type = type;
    }

    public MessageModel(String id, String msgKey, String time, String message, String reaction) {
        this.id = id;
        this.msgKey = msgKey;
        this.time = time;
        this.message = message;
        this.reaction = reaction;
    }

    public MessageModel(String id, String msgKey, String image, String time, String message,String reaction) {
        this.id = id;
        this.msgKey = msgKey;
        this.image = image;
        this.time = time;
        this.message = message;
        this.reaction = reaction;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
