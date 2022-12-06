package com.android.gossipgeese.model;

public class CallLogModel {
    String id, type, key;

    public CallLogModel() {
    }

    public CallLogModel(String id, String type, String key) {
        this.id = id;
        this.type = type;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
