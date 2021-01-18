package com.example.jamiaaty;

public class PostMember {
    String name, url ,postUri,time,uid,type,description,key_post;

    public PostMember(String name, String url, String postUri, String time, String uid, String type, String description) {
        this.name = name;
        this.url = url;
        this.postUri = postUri;
        this.time = time;
        this.uid = uid;
        this.type = type;
        this.description = description;
    }

    public String getKey_post() {
        return key_post;
    }

    public void setKey_post(String key_post) {
        this.key_post = key_post;
    }

    public PostMember() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPostUri() {
        return postUri;
    }

    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
