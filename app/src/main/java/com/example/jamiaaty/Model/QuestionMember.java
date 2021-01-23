package com.example.jamiaaty.Model;

public class QuestionMember {
    String name,url, userid,key,question,privacy,time;

    public QuestionMember(String name, String url, String userid, String key, String question, String privacy, String time) {
        this.name = name;
        this.url = url;
        this.userid = userid;
        this.key = key;
        this.question = question;
        this.privacy = privacy;
        this.time = time;
    }

    public QuestionMember() {
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
