package com.example.jamiaaty.Model;

public class All_UserMemeber {
    String name=""  , uid="" , prof="", url="",nameTolower="";
    String web ="";
    String email ="";


    public All_UserMemeber(String name, String uid, String prof, String url, String nameTolower, String web, String email) {
        this.name = name;
        this.uid = uid;
        this.prof = prof;
        this.url = url;
        this.nameTolower = nameTolower;
        this.web = web;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public All_UserMemeber() {
    }

    public String getNameTolower() {
        return nameTolower;
    }

    public void setNameTolower(String nameTolower) {
        this.nameTolower = nameTolower;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
