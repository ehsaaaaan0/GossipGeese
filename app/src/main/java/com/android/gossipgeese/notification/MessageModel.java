package com.android.gossipgeese.notification;

public class MessageModel {
    CharSequence text;
    long timeSpam;
    CharSequence sender;
    CharSequence recId;
    CharSequence sendId;

    public MessageModel() {
    }

    public MessageModel(CharSequence text,  CharSequence sender, CharSequence recId, CharSequence sendId) {
        this.text = text;
        this.sender = sender;
        this.recId = recId;
        this.sendId = sendId;
        this.timeSpam = System.currentTimeMillis();
    }
    public MessageModel(CharSequence text,  CharSequence sender) {
        this.text = text;
        this.sender = sender;
        this.timeSpam = System.currentTimeMillis();
    }

    public CharSequence getText() {
        return text;
    }

    public CharSequence getRecId() {
        return recId;
    }

    public void setRecId(CharSequence recId) {
        this.recId = recId;
    }

    public CharSequence getSendId() {
        return sendId;
    }

    public void setSendId(CharSequence sendId) {
        this.sendId = sendId;
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
