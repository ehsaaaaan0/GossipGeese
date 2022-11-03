package com.android.gossipgeese.notification;

public class MessageModel {
    CharSequence text;
    long timeSpam;
    CharSequence sender;

    public MessageModel() {
    }

    public MessageModel(CharSequence text,  CharSequence sender) {
        this.text = text;
        this.sender = sender;
        this.timeSpam = System.currentTimeMillis();
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public long getTimeSpam() {
        return timeSpam;
    }

    public void setTimeSpam(long timeSpam) {
        this.timeSpam = timeSpam;
    }

    public CharSequence getSender() {
        return sender;
    }

    public void setSender(CharSequence sender) {
        this.sender = sender;
    }
}
